# DenDen Backend Assignment

這是一個基於 Spring Boot 3.3 和 Java 21 的後端作業專案，包含 OCPI 互動流程圖設計與 RESTful API 實作。

## 技術堆疊 (Tech Stack)

- **Java Version**: 21 (LTS)
- **Framework**: Spring Boot 3.3.5
- **Database**: H2 In-Memory Database (開發測試用)
- **Build Tool**: Maven (包含 Maven Wrapper)
- **API Documentation**: Swagger UI / OpenAPI 3
- **Security**: BCrypt Password Hashing
- **Email Services**: 支援 Mock (預設), Mailjet, Brevo

## 快速開始 (Quick Start)

### 先決條件

- JDK 21+
- Maven 3.8+ (或直接使用專案內建的 `./mvnw`)

### 使用 Makefile (推薦)

本專案提供 `Makefile` 以簡化常用指令：

```bash
# 顯示說明與連結
make info

# 啟動應用程式 (預設 Port: 8080)
make run

# 執行所有測試 (單元測試 + 整合測試)
make test

# 清理並建置專案
make build

# Docker 部署
make docker-build  # 建置 Docker Image
make docker-run    # 啟動 Docker 容器 (背景執行)
make docker-stop   # 停止 Docker 容器
```

### 手動指令

若無法使用 Make，可使用 Maven Wrapper：

```bash
# 啟動
./mvnw spring-boot:run

# 執行測試
./mvnw test
```

## 設定說明 (Configuration)

### 1. 環境變數 (.env)

專案支援讀取 `.env` 檔案來設定敏感資訊。請複製範例檔案並修改：

```bash
cp .env.example .env
```

### 2. Email 服務設定

本專案支援多種 Email 發送模式，可於 `application.yml` 或 `.env` 中切換：

- **Mock 模式 (預設)**: 不會真正發信，僅在 Log 顯示內容。
  - 設定: `EMAIL_PROVIDER=mock`
- **Mailjet**:
  - 設定: `EMAIL_PROVIDER=mailjet`
  - 需在 `.env` 填入 `MAILJET_API_KEY`, `MAILJET_SECRET_KEY`, `MAILJET_SENDER_EMAIL`
- **Brevo**:
  - 設定: `EMAIL_PROVIDER=brevo`
  - 需在 `.env` 填入 `BREVO_API_KEY`, `BREVO_SENDER_EMAIL`

### 3. 資料庫 (H2 Console)

應用程式啟動後，可透過瀏覽器存取內建資料庫：
- **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: `password`

## 功能列表 (Features)

### 第一部分：互動流程圖設計 (OCPI 2.2.1)

位於 [doc/diagram.md](doc/diagram.md)。
- **內容**: 繪製 User 透過 SCSP 呼叫 EMSP 啟動 CPO 充電樁的時序圖 (Sequence Diagram)。
- **情境**: 涵蓋 Start Session, Stop Session, CDR (充電紀錄) 回傳與帳單通知流程。

### 第二部分：RESTful API 設計與實作

API 文件位於：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

1.  **會員註冊 (Registration)**
    - 使用 Email 註冊。
    - 密碼經過 BCrypt 加密儲存。
    - 註冊後發送開通 Email (含 Token)。
2.  **帳號開通 (Activation)**
    - 驗證 Email 中的 Token 以啟用帳號。
3.  **會員登入 (Login with 2FA)**
    - **第一階段**: 驗證 Email 與密碼。驗證通過後發送 6 位數 2FA 驗證碼至 Email。
    - **第二階段**: 驗證 2FA 驗證碼，通過後回傳 Session Token (Mock)。
4.  **查詢系統 (User Query)**
    - 查詢登入使用者的最後登入時間 (`lastLoginTime`)。

## 架構與設計 (Architecture & Design)

本專案採用 **Clean Architecture** 與 **SOLID** 原則進行設計，確保程式碼的可維護性與擴充性。

### 1. 分層架構 (Layered Architecture)
- **Controller Layer**: 處理 HTTP 請求與回應，使用 `ApiResponse<T>` 統一回傳格式。
- **Service Layer**: 包含核心業務邏輯 (如 2FA 流程、Token 驗證)。
- **Repository Layer**: 資料存取層 (使用 Spring Data JPA)。
- **DTO (Data Transfer Object)**: 定義 API 輸入輸出格式，避免直接暴露 Entity。

### 2. 設計模式與實踐 (Design Patterns & Practices)
- **Strategy Pattern**: 透過 `EmailService` 介面與多個實作 (`Mock`, `Mailjet`, `Brevo`)，實現可抽換的 Email 服務策略。
- **Global Exception Handling**: 使用 `@RestControllerAdvice` 與自定義 `AppException`，統一處理錯誤並回傳標準化的錯誤訊息。
- **Secure by Design**:
  - 使用 `BCrypt` 進行密碼雜湊。
  - 使用 `SecureRandom` 產生高強度 2FA 驗證碼。
  - 實作 `AuthInterceptor` 攔截器，確保只有驗證通過的請求能存取受保護資源。

### 3. 可觀測性 (Observability)
- **Logging**: 採用 Logback 進行日誌管理。
  - **Rolling Policy**: 每日自動滾動日誌檔案 (`application-yyyy-MM-dd.log`)。
  - **Error Isolation**: 獨立的 `error.log` 檔案，便於快速定位嚴重錯誤。
  - **Retention**: 自動清理 30 天前的日誌，避免磁碟空間耗盡。

### 4. 部署與維運 (DevOps)
- **Dockerization**: 提供 Multi-stage Dockerfile，優化映像檔大小與安全性。
- **Configuration Management**: 支援 `.env` 與環境變數注入，實現 **12-Factor App** 的配置原則。
- **Makefile**: 封裝常用指令，簡化開發與部署流程。

## 測試 (Testing)

專案包含完整的測試覆蓋：

- **單元測試 (Unit Tests)**: 針對 `AuthService` 業務邏輯進行測試 (Mock Repository & EmailService)。
- **整合測試 (Integration Tests)**: 針對 `AuthController` API 進行end to end測試 (使用 H2 DB)。

執行指令：
```bash
make test
```

## 部署與測試資源 (Deployment & Resources)

本專案已部署至 **Zeabur**，可直接進行線上測試：

- **線上網址**: [https://den-den-assignment.zeabur.app](https://den-den-assignment.zeabur.app)
- **Swagger UI**: [https://den-den-assignment.zeabur.app/swagger-ui/index.html](https://den-den-assignment.zeabur.app/swagger-ui/index.html)
- **Postman Collection**: 位於 [doc/DenDen Assignment API.postman_collection.json](doc/DenDen%20Assignment%20API.postman_collection.json)，可直接匯入 Postman 進行測試。
