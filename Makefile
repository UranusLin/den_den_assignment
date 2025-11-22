.PHONY: all build run test clean help info

# 嘗試載入 .env 檔案
ifneq (,$(wildcard ./.env))
    include .env
    export
endif

# 預設目標
all: info build

# 顯示專案資訊與幫助
info:
	@echo "================================================================"
	@echo "  DenDen Backend Assignment"
	@echo "================================================================"
	@echo "  [文件連結]"
	@echo "  - 作業需求: doc/requirements.txt"
	@echo "  - 時序圖 (Sequence Diagram): doc/diagram.md"
	@echo "  - API 文件 (啟動後): http://localhost:8080/swagger-ui.html"
	@echo "================================================================"

help: info
	@echo "  [可用指令]"
	@echo "  make build    - 清理並建置專案"
	@echo "  make run      - 啟動 Spring Boot 應用程式 (會讀取 .env)"
	@echo "  make test     - 執行單元測試與整合測試"
	@echo "  make clean    - 清理建置檔案"
	@echo "================================================================"

# 建置專案
build:
	./mvnw clean package -DskipTests

# 啟動應用程式
run:
	./mvnw spring-boot:run

# 執行測試
test:
	./mvnw test

# 清理
clean:
	./mvnw clean

# Docker 指令
docker-build:
	docker build -t denden-backend .

docker-run:
	docker-compose up -d

docker-stop:
	docker-compose down
