# 美股监控系统 (US Stock Monitor)

一个基于Spring Boot的美股新闻监控系统，通过RSS订阅实时监控股票动态，并支持多种消息推送渠道。

## 功能特性

- **RSS订阅监控**：自动抓取Stock Titan网站的RSS订阅源
- **多渠道通知**：支持Telegram Bot、微信机器人、钉钉机器人等多种消息推送
- **AI集成**：支持Spring AI MCP协议，提供智能助手功能
- **数据统计**：提供股票异动次数统计和查询功能
- **国际化**：集成百度翻译API，支持中文显示英文标题

## 技术栈

- **后端框架**：Spring Boot 4.0.0
- **编程语言**：Java 21
- **数据库**：MySQL 8.0.33
- **持久层框架**：MyBatis-Plus 3.5.14
- **数据库连接池**：HikariCP
- **构建工具**：Maven
- **RSS解析**：Rome (SyndFeed)
- **定时任务**：Spring Scheduling
- **AI集成**：Spring AI MCP (Model Context Protocol)
- **前端**：无（纯后端服务）

## 系统架构

本项目采用双模块架构：

- **stock-web**：主应用程序，负责RSS抓取、数据存储、消息推送等功能
- **stock-mcp**：AI模型上下文协议服务器，为AI助手提供工具调用能力

## 环境要求

- Java 21 或更高版本
- Maven 3.6.0 或更高版本
- MySQL 8.0 或更高版本
- Python 3.x (仅微信机器人功能需要)

## 安装部署

### 1. 克隆项目

```bash
git clone <repository-url>
cd us_stock_monitor_dev
```

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE us_stock_monitor_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库初始化脚本（如果有的话）

### 3. 环境配置

1. 修改数据库连接配置：
   - `stock-web/src/main/resources/application-dev.yml`
   - `stock-mcp/src/main/resources/application-dev.yml`

2. 配置环境变量，在 `.env` 文件中添加以下配置：
```env
# MySQL
MYSQL_HOST=localhost
MYSQL_PORT=5506
MYSQL_DATABASE=us_stock_monitor_dev
MYSQL_USERNAME=root
MYSQL_PASSWORD=root

# 百度翻译API
BAIDU_TRANSLATE_HOST=https://fanyi-api.baidu.com/api/trans/vip/translate
BAIDU_TRANSLATE_APPID=your_app_id
BAIDU_TRANSLATE_SECURITYKEY=your_security_key

# 钉钉机器人
DINGDING_TOKEN=your_dingding_token
DINGDING_SECRET=your_dingding_secret
DINGDING_USERID=your_userid

# Telegram Bot（可选）
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_CHAT_ID=your_chat_id
```

### 4. 构建项目

```bash
mvn clean install
```

### 5. 启动服务

#### 启动Web服务
```bash
cd stock-web
mvn spring-boot:run
```
默认端口：6060

#### 启动MCP服务（AI助手服务）
```bash
cd stock-mcp
mvn spring-boot:run
```
默认端口：7070

## 配置说明

### RSS监控配置
- 监控频率：默认每30秒检查一次
- RSS源：https://www.stocktitan.net/rss
- 定时任务配置在 `StockScheduler.java` 中

### 消息推送配置

#### Telegram Bot
1. 创建Telegram Bot并获取Token
2. 配置 `TELEGRAM_BOT_TOKEN` 和 `TELEGRAM_CHAT_ID`
3. 在 `TelegramBotServiceImpl.java` 中修改相关配置

#### 微信机器人
1. 安装Python 3.x环境
2. 安装wxauto库：`pip install wxauto`
3. 仅支持Windows系统
4. 仅支持微信3.9.x或低版本的4.0.x

#### 钉钉机器人
1. 在钉钉群中添加自定义机器人
2. 配置 `DINGDING_TOKEN` 和 `DINGDING_SECRET`

### 邮件服务配置（MCP模块）
在 `stock-mcp/src/main/resources/application.yml` 中配置SMTP邮件服务

## API接口

系统提供了多种股票数据查询接口，支持按股票代码、时间范围、关键词等方式查询。

## 开发说明

### 项目结构

```
us_stock_monitor_dev/
├── stock-web/              # 主应用程序
│   ├── src/main/java/com/itzixi/
│   │   ├── controller/     # 控制器
│   │   ├── entity/         # 实体类
│   │   ├── enums/          # 枚举类
│   │   ├── mapper/         # 数据访问层
│   │   ├── service/        # 业务逻辑层
│   │   ├── utils/          # 工具类
│   │   ├── Application.java # 启动类
│   │   └── StockScheduler.java # 定时任务
│   └── src/main/resources/
├── stock-mcp/              # AI MCP服务
│   ├── src/main/java/com/itzixi/
│   │   ├── mcp/tool/       # MCP工具类
│   │   └── service/        # 服务实现
│   └── src/main/resources/
└── pom.xml                 # 父项目配置
```

### 核心功能模块

- **RssService**：RSS订阅源处理
- **StockService**：股票数据处理
- **TelegramBotService**：Telegram消息推送
- **WechatBotService**：微信消息推送
- **StockScheduler**：定时任务调度
- **MCP Tools**：AI助手工具集

## 注意事项

1. **微信机器人限制**：
   - 仅支持Windows系统
   - 仅支持特定版本的微信客户端
   - 请遵守微信使用协议，勿用于商业或非法用途

2. **数据库连接**：
   - 确保MySQL服务正常运行
   - 数据库用户具有相应权限

3. **外部API**：
   - 百度翻译API需要申请相应的APP ID和密钥
   - Telegram Bot需要创建并配置机器人

4. **定时任务**：
   - 默认每30秒抓取一次RSS数据
   - 可根据需要调整 `StockScheduler.java` 中的cron表达式

## 常见问题

### 如何修改定时任务间隔？
编辑 `StockScheduler.java` 文件中的 `@Scheduled(cron = "*/30 * * * * ?")` 注解。

### 如何添加新的RSS源？
修改 `RssServiceImpl.java` 中的 `RSS_URL` 常量。

### 如何扩展通知渠道？
实现 `TelegramBotService` 接口并添加相应的服务实现类。

## 许可证

本项目仅供学习交流使用，请勿用于商业用途。

## 免责声明

代码仅用于技术交流学习，请勿用于非法用途和商业用途！如因此产生任何法律纠纷甚至造成账号封禁或其他损失等情况，使用者承担所有责任。