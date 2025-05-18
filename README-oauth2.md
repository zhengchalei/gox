# OAuth2 第三方登录配置说明

## 1. 配置概述

Gox 系统支持多种第三方平台的 OAuth2 登录，包括 GitHub、Gitee、微信、QQ、微博和钉钉等。通过配置相应的客户端 ID、密钥和回调地址，可以轻松实现第三方账号登录和绑定功能。

## 2. 配置方式

### 2.1 配置文件

系统使用 `application-auth.yml` 文件进行 OAuth2 配置，遵循以下格式：

```yaml
gox:
  oauth2:
    平台名称:
      client-id: 客户端ID
      client-secret: 客户端密钥
      redirect-uri: 回调地址
```

### 2.2 环境变量配置

为了提高安全性，建议通过环境变量来配置敏感信息：

```yaml
gox:
  oauth2:
    github:
      client-id: ${GITHUB_CLIENT_ID:默认值}
      client-secret: ${GITHUB_CLIENT_SECRET:默认值}
      redirect-uri: ${GITHUB_REDIRECT_URI:默认回调地址}
```

## 3. 回调地址说明

每个平台的回调地址必须符合以下格式：

```
http://域名/api/auth/oauth/callback/平台名称
```

例如：`http://localhost:8080/api/auth/oauth/callback/github`

> **重要提示**：回调地址必须与平台应用配置中设置的回调地址一致，否则授权将失败。

## 4. 支持的平台

目前支持的第三方平台包括：

- GitHub
- Gitee
- 微信
- QQ
- 微博
- 钉钉

## 5. 配置示例

```yaml
gox:
  oauth2:
    github:
      client-id: your-github-client-id
      client-secret: your-github-client-secret
      redirect-uri: http://localhost:8080/api/auth/oauth/callback/github
    gitee:
      client-id: your-gitee-client-id
      client-secret: your-gitee-client-secret
      redirect-uri: http://localhost:8080/api/auth/oauth/callback/gitee
```

## 6. 应用获取方式

### 6.1 GitHub

1. 访问 [GitHub Developer Settings](https://github.com/settings/developers)
2. 创建一个新的 OAuth App
3. 填写应用名称、主页 URL 和回调 URL
4. 创建后获取 Client ID 和 Client Secret

### 6.2 Gitee

1. 访问 [Gitee 第三方应用](https://gitee.com/oauth/applications)
2. 创建一个新应用
3. 填写应用名称、主页 URL 和回调 URL
4. 创建后获取 Client ID 和 Client Secret

### 6.3 其他平台

其他平台的应用获取方式请参考对应平台的开发者文档。

## 7. 接口说明

### 7.1 无需登录的接口

- `GET /api/oauth/render/{source}`: 获取第三方登录授权地址
- `GET /api/oauth/callback/{source}`: 第三方登录回调接口

### 7.2 需要登录的接口

- `GET /api/auth/oauth/my-bindings`: 获取当前登录用户绑定的所有 OAuth2 账号
- `GET /api/auth/oauth/bind-url/{source}`: 获取当前登录用户绑定第三方平台的授权地址
- `DELETE /api/auth/oauth/unbind/{source}`: 当前登录用户解绑 OAuth2 账号
- `GET /api/auth/oauth/is-bound/{source}`: 检查当前登录用户是否已绑定指定平台 