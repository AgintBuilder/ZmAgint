# Docker Compose 部署指南

本文档介绍如何使用 Docker Compose 将项目部署到服务器。

## 项目结构

```
us_stock_monitor_dev/          ← 父项目 (pom)
├── pom.xml
├── stock-common/              ← 公共模块 (jar)
├── stock-web/                 ← Web 应用 (jar，端口 6060)
├── stock-mcp/                 ← MCP 服务 (jar，端口 7070)
├── docker-compose.yml         ← Docker Compose 配置
├── DEPLOY.md                  ← 本文档
└── docker/
    └── docker-compose.yml     ← 本地开发用（仅 MySQL + Redis）
```

## 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                      服务器                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Docker Compose                      │   │
│  │                                                  │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │   │
│  │  │  MySQL   │  │  Redis   │  │  stock-web   │  │   │
│  │  │  :3306   │  │  :6379   │  │   :6060      │  │   │
│  │  └────┬─────┘  └────┬─────┘  └──────┬───────┘  │   │
│  │       │             │               │          │   │
│  │       └─────────────┴───────────────┘          │   │
│  │                     │                          │   │
│  │              ┌──────────────┐                  │   │
│  │              │  stock-mcp   │                  │   │
│  │              │   :7070      │                  │   │
│  │              └──────────────┘                  │   │
│  │                     │                          │   │
│  │              ┌──────────────┐                  │   │
│  │              │  app-network │ (bridge)         │   │
│  │              └──────────────┘                  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

## 服务说明

| 服务 | 端口 | 依赖 | 说明 |
|------|------|------|------|
| mysql | 3306 | - | MySQL 9.5.0 数据库 |
| redis | 6379 | - | Redis 7 缓存 |
| stock-web | 6060 | mysql, redis | Web 应用服务 |
| stock-mcp | 7070 | mysql | MCP 服务 |

## 部署步骤

### 第一步：准备服务器

```bash
# 1. 安装 Docker
curl -fsSL https://get.docker.com | sh

# 2. 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 3. 验证安装
docker --version
docker-compose --version
```

### 第二步：上传项目文件

在服务器上创建目录：

```bash
mkdir -p /opt/stock-monitor
cd /opt/stock-monitor
```

上传以下文件到服务器：

```
/opt/stock-monitor/
├── docker-compose.yml          # Docker Compose 配置文件
├── .env                        # 环境变量文件（需要创建）
├── stock-web/
│   ├── Dockerfile              # stock-web 镜像构建文件
│   └── target/
│       └── stock-web-1.0-SNAPSHOT.jar
└── stock-mcp/
    ├── Dockerfile              # stock-mcp 镜像构建文件
    └── target/
        └── stock-mcp-1.0-SNAPSHOT.jar
```

### 第三步：创建环境变量文件

在服务器上创建 `.env` 文件：

```bash
cat > /opt/stock-monitor/.env << 'EOF'
# 百度翻译 API 配置
BAIDU_TRANSLATE_HOST=https://fanyi-api.baidu.com/api/trans/vip/translate
BAIDU_TRANSLATE_APPID=your_appid_here
BAIDU_TRANSLATE_SECURITYKEY=your_security_key_here

# 钉钉机器人配置
DINGDING_TOKEN=your_dingding_token_here
DINGDING_SECRET=your_dingding_secret_here
DINGDING_USERID=your_dingding_userid_here
EOF
```

**注意**：请将 `your_*_here` 替换为实际的配置值。

### 第四步：本地打包项目

在本地开发机器执行：

```bash
# 进入项目根目录
cd us_stock_monitor_dev

# 打包所有模块（跳过测试）
mvn clean package -DskipTests -pl stock-common,stock-web,stock-mcp -am
```

打包完成后，会生成以下文件：
- `stock-web/target/stock-web-1.0-SNAPSHOT.jar`
- `stock-mcp/target/stock-mcp-1.0-SNAPSHOT.jar`

### 第五步：上传 JAR 文件到服务器

使用 scp 或其他工具上传：

```bash
# 上传 stock-web JAR
scp stock-web/target/stock-web-1.0-SNAPSHOT.jar \
    root@your-server-ip:/opt/stock-monitor/stock-web/target/

# 上传 stock-mcp JAR
scp stock-mcp/target/stock-mcp-1.0-SNAPSHOT.jar \
    root@your-server-ip:/opt/stock-monitor/stock-mcp/target/
```

### 第六步：启动服务

在服务器上执行：

```bash
cd /opt/stock-monitor

# 构建镜像并启动所有服务（后台运行）
docker-compose up -d --build

# 查看启动状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 第七步：验证部署

```bash
# 1. 检查容器运行状态
docker-compose ps

# 2. 查看 stock-web 日志
docker-compose logs -f stock-web

# 3. 查看 stock-mcp 日志
docker-compose logs -f stock-mcp

# 4. 测试接口（在服务器上执行）
curl http://localhost:6060

# 5. 测试 MCP 服务
curl http://localhost:7070
```

## 常用命令

| 命令 | 说明 |
|------|------|
| `docker-compose up -d` | 后台启动所有服务 |
| `docker-compose up -d --build` | 重新构建镜像并启动 |
| `docker-compose down` | 停止并删除容器 |
| `docker-compose down -v` | 停止并删除容器和数据卷 |
| `docker-compose restart stock-web` | 重启单个服务 |
| `docker-compose logs -f` | 实时查看所有日志 |
| `docker-compose logs -f stock-web` | 查看指定服务日志 |
| `docker-compose exec mysql bash` | 进入 MySQL 容器 |
| `docker-compose exec redis redis-cli` | 进入 Redis CLI |
| `docker-compose pull` | 更新镜像 |

## 数据持久化

数据通过 Docker Volumes 持久化到宿主机：

| 服务 | 宿主机路径 | 容器路径 | 说明 |
|------|-----------|---------|------|
| MySQL | `./docker/mysql/data` | `/var/lib/mysql` | 数据库文件 |
| MySQL | `./docker/mysql/log` | `/var/log/mysql` | 日志文件 |
| Redis | `./docker/redis/data` | `/data` | Redis 数据 |

**注意**：删除容器时数据不会丢失，除非使用 `docker-compose down -v`。

## 环境变量说明

### stock-web 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SPRING_DATASOURCE_URL` | MySQL 连接地址 | jdbc:mysql://mysql:3306/us_stock_monitor_dev |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | root |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | root |
| `SPRING_DATA_REDIS_HOST` | Redis 主机 | redis |
| `SPRING_DATA_REDIS_PORT` | Redis 端口 | 6379 |
| `BAIDU_TRANSLATE_HOST` | 百度翻译 API 地址 | - |
| `BAIDU_TRANSLATE_APPID` | 百度翻译 APP ID | - |
| `BAIDU_TRANSLATE_SECURITYKEY` | 百度翻译密钥 | - |
| `DINGDING_TOKEN` | 钉钉机器人 Token | - |
| `DINGDING_SECRET` | 钉钉机器人密钥 | - |
| `DINGDING_USERID` | 钉钉用户 ID | - |

### stock-mcp 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SPRING_DATASOURCE_URL` | MySQL 连接地址 | jdbc:mysql://mysql:3306/us_stock_monitor_dev |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | root |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | root |

## 故障排查

### 1. 容器无法启动

```bash
# 查看详细日志
docker-compose logs stock-web

# 检查环境变量是否正确加载
docker-compose exec stock-web env
```

### 2. 数据库连接失败

```bash
# 检查 MySQL 是否健康
docker-compose ps

# 进入 MySQL 容器检查
docker-compose exec mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

### 3. 端口被占用

```bash
# 检查端口占用
netstat -tlnp | grep 6060

# 修改 docker-compose.yml 中的端口映射
# 例如：将 "6060:6060" 改为 "8080:6060"
```

### 4. 重新部署

```bash
# 停止并删除所有容器
docker-compose down

# 重新构建并启动
docker-compose up -d --build
```

## 更新部署

当代码更新后，重新部署的步骤：

```bash
# 1. 本地重新打包
mvn clean package -DskipTests -pl stock-common,stock-web,stock-mcp -am

# 2. 上传新的 JAR 文件到服务器
scp stock-web/target/stock-web-1.0-SNAPSHOT.jar root@server:/opt/stock-monitor/stock-web/target/
scp stock-mcp/target/stock-mcp-1.0-SNAPSHOT.jar root@server:/opt/stock-monitor/stock-mcp/target/

# 3. 在服务器上重新构建并启动
cd /opt/stock-monitor
docker-compose up -d --build

# 4. 查看更新后的日志
docker-compose logs -f stock-web
```

## 安全建议

1. **修改默认密码**：生产环境请修改 MySQL 的 root 密码
2. **使用 HTTPS**：对外暴露的服务建议使用 HTTPS
3. **限制端口访问**：使用防火墙限制端口访问范围
4. **定期备份**：定期备份 MySQL 数据

## 参考文档

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 官方文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 部署](https://spring.io/guides/topicals/spring-boot-docker/)
