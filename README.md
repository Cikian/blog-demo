Github仓库地址：https://github.com/Cikian/blog-demo.git

后端api地址：[blog-demo.cikian.cn/api/](http://blog-demo.cikian.cn/api/auth/me),，例如，获取当前用户信息接口：[blog-demo.cikian.cn/api/auth/me](http://blog-demo.cikian.cn/api/auth/me)

## 一、基本思路

demo功能比较简单，主要在于“鉴权”，以及常规的CURD操作。由于博客系统特点，还实现了**token的无感刷新,**保证用户在正常浏览博客时，不会由于token过期而重新登录。

当用户请求需要权限认证的接口时，需要判断其权限，例如”获取当前用户信息“、”发布文章“等接口，需要用户当前为登录状态；”更新文章“、”删除文章“等接口除了需要用户为登录状态，还需要判断目标文章是否属于当前登录用户；而”登录“、”注册“接口则不需要鉴权。

一个简单有效的办法是使用token作为鉴权的依据，**由于demo功能比较简单，所以手写了一个简单的token生成与解析机制**，未使用JWT。

> *使用JWT简单直接*

在用户请求接口时，在拦截器中判断目标接口是否需要鉴权，这里使用自定义注解实现判断。不需要鉴权的直接放行，反之执行鉴权操作。

此demo中的具体业务逻辑除了一些简单的权限判断外，基本为简单的CURD操作，下文不过多赘述。

## 二、技术架构

Spring Boot + Mybatis Plus

数据库：MySQL

## 三、具体实现

### 1. token生成与解析

token的作用主要是”标识用户“，用作身份验证。一般token主要有两大部分组成：1. 用户信息（载荷），2. token本身的安全信息（Herder和Signature）。前者用于存储用户信息，后者主要为了保证token本身不被篡改等。

**生成token：**用户登录成功后，将用户信息转换为json字符串，根据一定规则拼接后经过Base64编码生成的字符串作为token的载荷。

将密钥、机构、过期时间等信息加密后再经过Base64编码后得到的字符串作为token的”签名“部分。两段字符串再拼接（以”.“分割）后得到完整token，代码如下：

```java
public static String generateToken(User user, int refreshTime) {
    String userId = user.getUserId();
    Long expireTime = System.currentTimeMillis() + (BASE_TIME * refreshTime);
    TokenAuth tokenAuth = new TokenAuth(userId + TOKEN_AUTH, expireTime);
    String authStr = tokenAuth.toString();
    String tokenHeaderBase64 = Base64.getEncoder().encodeToString(userId.getBytes());
    String tokenAuthBase64 = Base64.getEncoder().encodeToString(authStr.getBytes());
    return tokenHeaderBase64 + "." + tokenAuthBase64;
}
```

其中`BASE_TIME`为固定值1000 * 60 * 60毫秒（1小时），`refreshTime`从配置文件中获取，为自定义值。例如`refreshTime`为1时，token过期时间为1小时，为2时，则过期时间为2小时。

**解析token：**解析token首先将载荷与签名分开，签名部分若解析失败则token经过篡改，组织请求。载荷部分解析后经过得到用户信息的字符串，反序列化为Java对象。然后进行权限判断与超时判断，例如超时后抛出自定义异常`TokenException`，在全局异常处理时将错误信息返回前端。

```java
public static String parseToken(String token) throws TokenException {
    TokenAuth tokenAuthObj;
    String tokenHeader;
    try {
        String[] tokenArr = token.split("\\.");
        tokenHeader = new String(Base64.getDecoder().decode(tokenArr[0]));
        String tokenAuth = new String(Base64.getDecoder().decode(tokenArr[1]));
        tokenAuthObj = TokenAuth.parse(tokenAuth);
    } catch (Exception e) {
        throw new TokenException("token无效，请重新登录");
    }
    if (!tokenAuthObj.getStr().equals(tokenHeader + TOKEN_AUTH)) {
        throw new TokenException("违法认证，请重新登录");
    }
    if (tokenAuthObj.getTimestamp() < System.currentTimeMillis()) {
        throw new TokenException("token过期，请重新登录");
    }
    return tokenHeader;
}
```

### 2. 无感刷新token

在拦截器中，获取并成功解析token后判断剩余过期时间是否小于设置的刷新token时间（时间在配置文件中自定义），若是，则重新生成token并放到响应的header中，前端可判断有无新token，进而更新用户token。因为大部分请求都要经过拦截器（用户登陆后查看文章也会携带token），所以当用户进行读文章、发文章等操作时便会刷新token，这对用户来说是无感知的。代码如下：

```java
String token = request.getHeader("token");
if (token != null) {
	Long remainingTime = TokenUtils.getRemainingTime(token);
	if (remainingTime < 1000L * 60 * FLUSH_TIME) {
	String uid = TokenUtils.parseToken(token);
	// 刷新 token
	String newToken = TokenUtils.generateToken(new User(uid), TOKEN_TIMEOUT);
	response.setHeader("new-token", newToken);
	}
}
response.setHeader("Access-Control-Allow-Origin", "new-token");
```

其中，`FLUSH_TIME`来自配置文件，可自定义。

### 3. 接口鉴权

自定义了两个注解：`@NoAuth`和`@NeedLogin`，例如在需要用户登录才能请求的接口上（如发布文章，获取用户信息接口），使用`@NeedLogin`注解。注册登录接口使用`@NoAuth`接口。

在当请求进去拦截器时，获取请求的接口是否使用以上注解，根据不同注解执行响应的鉴权操作。代码如下：

```java
boolean securityEnabled = environment.getProperty("blog.security.enabled", Boolean.class, true);

if (!securityEnabled) {
	return true;
}

if (handler instanceof HandlerMethod handlerMethod) {
	NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
	if (noAuth != null) {
		return true;
	}

	NeedLogin needLogin = handlerMethod.getMethodAnnotation(NeedLogin.class);
	if (needLogin != null) {
		String msg = isLogin(request);
		if (msg.equals("请先登录") || msg.equals("登录已过期，请重新登录")) {
			throw new NoLoginException(msg);
		} else {
			setHeader(request, "userId", msg);
		}
	}
}
```

其中`securityEnabled`为配置文件中自定义的开启鉴权开关，可选择开启或关闭，关闭后则全部接口的全部请求直接放行。

`setHeader()`方法为token解析成功后，利用反射将用户id放入请求的header中，方便之后业务逻辑使用，具体实现如下：

```java
private void setHeader(HttpServletRequest request, String key, String value){
	Class<? extends HttpServletRequest> requestClass = request.getClass();
	try {
		Field request1 = requestClass.getDeclaredField("request");
		request1.setAccessible(true);
		Object o = request1.get(request);
		Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
		coyoteRequest.setAccessible(true);
		Object o1 = coyoteRequest.get(o);
		Field headers = o1.getClass().getDeclaredField("headers");
		headers.setAccessible(true);
		MimeHeaders o2 = (MimeHeaders)headers.get(o1);
		o2.removeHeader(key);
		o2.addValue(key).setString(value);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
```



> 自定义配置：
>
> ```yaml
> blog:
> security:
>  enabled: true # 权限认证开关，默认为true
>  token-timeout: 3 # token超时时间，默认3小时，单位：小时
>  token-refresh-per: 30 # token过期前多久刷新，默认20分钟，单位：分钟
> ```

### 5. 密码加密

为保证密码安全，一般来说，用户输入明文密码后，前端进行第一次加密，传入服务端时，首先对前端加密后的密文”加盐“，然后再加密。所有加密算法使用md5加密，代码如下：

```java
public Result addUser(User user) {
	User userByUserName = userMapper.getUserByUserName(user.getUsername());
	if (userByUserName != null) {
		return new Result(ResultCode.FAIL.getCode(), "用户名重复");
	}
	String password = user.getPassword();
	// 加盐
	password = password + "cikian";
	// md5加密
	String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
	user.setPassword(md5Password);

	user.setCreated(LocalDateTime.now());
	user.setLastModified(LocalDateTime.now());
	int i = userMapper.insert(user);
	if (i > 0) {
		return new Result(ResultCode.SUCCESS.getCode(), "注册成功");
	}
	return new Result(ResultCode.FAIL.getCode(), "注册失败，请稍后再试");
}
```

其他功能不做过多赘述。

## 四、结果展示

1. 注册

   发送数据：

   ![](https://s2.loli.net/2024/07/19/Dvhm9nkMyLx3NGC.png)

   结果：

   ![](https://s2.loli.net/2024/07/19/3sxwYjCtkbO4DG5.png)

2. 登录成功：

   ![](https://s2.loli.net/2024/07/19/udB3sIWZiDn8zjr.png)

3. 未登录请求需要登录接口：

   ![](https://s2.loli.net/2024/07/19/AwyHUTxsdj2tWSO.png)

4. token过期：

   ![](https://s2.loli.net/2024/07/19/FBWyK7RbO3VaoHG.png)

5. 分页查询：

   ![](https://s2.loli.net/2024/07/19/s843jKlF5JAtWNv.png)

6. 刷新token时的响应头：

   ![](https://s2.loli.net/2024/07/19/YacidIOryS8PoDJ.png)

   

   

   

   

