# Blog-Test

一个基于 **Spring Boot 3** + **MySQL 8** + **Thymeleaf** 构建的个人全栈博客系统。

## 技术栈

| 层次 | 技术 |
|---|---|
| 后端框架 | Spring Boot 3.4.3 |
| 安全框架 | Spring Security（Session 认证 + BCrypt 密码加密） |
| ORM | Spring Data JPA + Hibernate |
| 模板引擎 | Thymeleaf + Thymeleaf Layout Dialect |
| 数据库 | MySQL 8.0 |
| 前端框架 | Bootstrap 5.3 |
| Markdown 编辑器 | EasyMDE（管理后台） |
| Markdown 渲染 | showdown.js（前端渲染） |
| 代码高亮 | highlight.js |
| 构建工具 | Maven |
| JDK | Java 17 |

## 功能概览

### 前台页面

| 页面 | 说明 |
|---|---|
| 首页 `/` | 分页展示已发布文章，支持置顶 |
| 文章详情 `/posts/{slug}` | Markdown 渲染、代码高亮、评论 |
| 分类筛选 `/categories/{slug}` | 按分类查看文章 |
| 标签筛选 `/tags/{slug}` | 按标签查看文章 |
| 关于页面 `/about` | 个人介绍（Markdown） |
| 搜索 `/search?q=关键词` | 全文搜索文章标题和内容 |

### 管理后台 `/admin`

| 功能 | 说明 |
|---|---|
| 仪表盘 | 文章数、评论数、分类数统计 |
| 文章管理 | 新建 / 编辑 / 删除 / 发布 / 归档 |
| 分类管理 | 分类增删改，支持排序 |
| 标签管理 | 标签增删改 |
| 评论审核 | 通过 / 标记垃圾 / 删除 |
| 关于管理 | 编辑个人介绍页面 |

### 安全特性

- Session 表单登录（`/admin/login`）
- BCrypt 密码加密
- CSRF 防护
- Remember-Me（14天）
- 未登录自动跳转登录页

## 快速开始

### 环境要求

- **JDK 17** 或更高版本
- **MySQL 8.0** 或更高版本
- **Maven 3.9** 或更高版本

### 1. 创建数据库

```sql
CREATE DATABASE blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: root          # 改为你的数据库用户名
    password: 123456        # 改为你的数据库密码
```

也可以通过环境变量配置：

```bash
export DB_HOST=localhost
export DB_NAME=blog
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

### 3. 启动应用

```bash
# 编译并运行
mvn spring-boot:run

# 或者先打包再运行
mvn clean package -DskipTests
java -jar target/blog-1.0.0.jar
```

### 4. 访问

| 地址 | 说明 |
|---|---|
| `http://localhost:8080` | 博客首页 |
| `http://localhost:8080/admin/login` | 管理后台登录 |

### 5. 默认管理员账号

- **用户名**：`admin`
- **密码**：`admin123`

> 首次启动会自动创建管理员账号、默认分类和关于页面。**上线前请务必修改默认密码！**

## 项目结构

```
src/main/java/com/blog/
├── BlogApplication.java          # 应用入口
├── config/                       # 配置类
│   ├── SecurityConfig.java       # Spring Security 安全配置
│   ├── WebConfig.java            # Web MVC 配置（静态资源映射）
│   └── DataInitializer.java      # 数据初始化（默认管理员等）
├── controller/                   # 控制器
│   ├── HomeController.java       # 首页
│   ├── PostController.java       # 文章详情 + 评论
│   ├── CategoryController.java   # 分类筛选
│   ├── TagController.java        # 标签筛选
│   ├── AboutController.java      # 关于页面
│   ├── SearchController.java     # 搜索
│   └── admin/                    # 管理后台控制器
│       ├── AuthController.java
│       ├── AdminController.java
│       ├── AdminPostController.java
│       ├── AdminCategoryController.java
│       ├── AdminTagController.java
│       ├── AdminCommentController.java
│       └── AdminAboutController.java
├── entity/                       # JPA 实体
│   ├── User.java                 # 管理员用户（实现 UserDetails）
│   ├── Post.java                 # 文章
│   ├── Category.java             # 分类
│   ├── Tag.java                  # 标签
│   ├── Comment.java              # 评论（支持嵌套回复）
│   ├── About.java                # 关于页面
│   ├── PostStatus.java           # 文章状态枚举
│   └── CommentStatus.java        # 评论状态枚举
├── repository/                   # Spring Data JPA 仓库
├── service/                      # 业务逻辑层
├── dto/                          # 表单数据传输对象
├── exception/                    # 异常处理
└── util/                         # 工具类（Slug 生成、Markdown 转换）

src/main/resources/
├── application.yml               # 应用配置
├── templates/                    # Thymeleaf 模板
│   ├── layouts/                  # 布局模板
│   ├── fragments/                # 片段（导航、侧边栏、分页等）
│   ├── home.html                 # 首页
│   ├── about.html                # 关于页
│   ├── search.html               # 搜索页
│   ├── post/detail.html          # 文章详情
│   ├── category/posts.html       # 分类文章列表
│   ├── tag/posts.html            # 标签文章列表
│   ├── admin/                    # 管理后台模板
│   └── error/                    # 错误页（404、500）
└── static/                       # 静态资源
    ├── css/style.css
    └── js/
        ├── main.js               # 前台 JS
        └── admin.js              # 管理后台 JS
```

## 数据库表结构

| 表名 | 说明 |
|---|---|
| `users` | 管理员用户 |
| `posts` | 文章（FULLTEXT 索引支持全文搜索） |
| `categories` | 分类 |
| `tags` | 标签 |
| `post_tags` | 文章-标签关联表 |
| `comments` | 评论（自引用支持嵌套回复） |
| `about` | 关于页面（单例记录） |

## License

MIT
