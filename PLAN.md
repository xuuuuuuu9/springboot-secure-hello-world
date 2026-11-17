# 后端笔试实施计划

## 目标

使用 **Spring Boot 3.2.0** 实现一个需要 Spring Security 身份认证的 `GET` Hello World 接口，以及用户名密码登录接口。按题目使用示例账号 `test` / `123456`。完成后提交独立的 GitHub 仓库链接。

## 方案选择

采用 **Java 17 + Maven + Spring Boot 3.2.0 + Spring Security**。Java 17 是 Spring Boot 3.2.0 的最低要求。项目放在 `backend/`，作为独立仓库交付，避免与第一题前端代码混在同一个提交中。

认证采用 **服务端 Session**：客户端向登录接口提交 JSON 用户名和密码；认证成功后由 Spring Security 保存登录状态，浏览器或 `curl` 在后续请求中携带 `JSESSIONID`。本题只要求固定账号和两个接口，因此不引入数据库、JWT 或额外身份服务。

| 接口 | 请求 | 预期响应 |
| --- | --- | --- |
| 登录 | `POST /api/login`，JSON：`{"username":"test","password":"123456"}` | `200`，返回成功信息并设置 Session Cookie |
| 登录失败 | 用户名或密码错误 | `401`，返回明确的失败信息，不建立已认证状态 |
| Hello World | `GET /api/hello`，携带有效 Session Cookie | `200`，`text/plain`，响应体严格为 `Hello World` |
| 未登录访问 | `GET /api/hello`，无有效 Session Cookie | `401` |

为使 JSON 登录接口能够直接通过 `curl` 验证，安全配置将仅针对本示例 API 关闭 CSRF 防护；README 会注明这是笔试演示配置。后续若扩展为面向浏览器的真实应用，应补上 CSRF 方案。密码在内存用户配置中使用 `PasswordEncoder` 编码，不在认证逻辑中比较明文字符串。

## 文件规划

| 文件 | 职责 |
| --- | --- |
| `pom.xml` | 锁定 Spring Boot `3.2.0`，引入 Web、Security、Test 依赖 |
| `src/main/java/.../BackendApplication.java` | Spring Boot 启动入口 |
| `src/main/java/.../SecurityConfig.java` | 固定用户、密码编码器、认证规则、401 入口和 Session 配置 |
| `src/main/java/.../AuthController.java` | 接收登录请求，调用认证管理器并保存 SecurityContext |
| `src/main/java/.../HelloController.java` | 返回 `Hello World` |
| `src/test/java/.../ApiSecurityTest.java` | 验证登录成功、登录失败、未登录拦截及认证后访问 |
| `README.md` | 启动、测试和 `curl` 演示命令 |

## 实施步骤

### 1. 建立独立项目

- [x] 在 `backend/` 初始化 Maven 项目，固定 Spring Boot 版本为 `3.2.0`，Java 版本为 `17`。
- [x] 加入 Spring Web、Spring Security、Spring Boot Test 和 Security Test 依赖。
- [x] 配置 `.gitignore`，排除 `target/`、IDE 文件和本地配置。

**验收**：`mvn test` 能启动 Spring 上下文。

### 2. 先定义接口测试

- [x] 用 MockMvc 写出四类测试：匿名访问为 `401`；正确登录为 `200` 并获得 Session；错误密码为 `401`；携带登录 Session 访问返回精确的 `Hello World`。
- [x] 运行测试，确认缺少实现时测试失败。

**验收**：测试明确约束请求方法、路径、状态码和响应体。

### 3. 实现认证与 Hello World

- [x] 配置内存用户 `test`，密码为编码后的 `123456`。
- [x] 配置 `/api/login` 允许匿名访问，`/api/hello` 必须已认证，其余请求按最小权限处理。
- [x] 登录成功时显式保存 `SecurityContext` 到 Session；认证失败返回 `401`。
- [x] Hello World 接口返回 `text/plain` 的 `Hello World`。

**验收**：步骤 2 的测试全部通过；重复请求能复用 Session，错误登录不能访问受保护接口。

### 4. 运行与交付检查

- [x] 运行 `mvn test` 和 `mvn package`。
- [x] 启动应用，用 `curl` 的 cookie jar 演示“先登录、后访问”的完整流程，并检查未登录、错误密码场景。
- [x] 编写 README，包含 Java/Maven 版本、启动命令、接口说明、示例账号和 Session 使用方式。
- [x] 在 `backend/` 建立独立 Git 仓库，提交源码和 README，推送到 `xuuuuuuu9` 账号下的独立公开仓库，核对链接可访问。

**验收**：评审能克隆仓库、运行测试，并按 README 得到精确的 `Hello World` 响应。

## 不在本题范围内

用户注册、数据库持久化、JWT、权限角色管理、完整前端登录页和部署上线。若题目后续补充这些要求，再单独调整方案。
