ifeq ($(OS), Windows_NT)
	MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew.bat
else
	MAKEFILE_PATH := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew
endif

GRADLE_DEFAULT_PARAMETERS := --console=plain

.DEFAULT_GOAL := usage

.PHONY: usage \
	clean purge rebuild-build-logic compile build install publish test \
	wrapper \
	docker-build docker-remove-dangling docker-purge

.SILENT:

usage:
	echo "Usage: make [target]"
	echo ""
	echo "  clean                   删除构建产物"
	echo "  purge                   彻底清理(含 Gradle 缓存)"
	echo "  rebuild-build-logic     重建 buildSrc 构建逻辑"
	echo "  compile                 编译主代码"
	echo "  build                   编译并打包"
	echo "  install                 发布到本地 Maven 仓库"
	echo "  publish                 发布到 Maven 中央仓库"
	echo "  test                    运行测试"
	echo "  wrapper                 升级 Gradle wrapper"
	echo "  docker-build            构建 docker 镜像"
	echo "  docker-remove-dangling  删除孤儿镜像"
	echo "  docker-purge            删除未使用的镜像"

clean:
	$(GRADLEW) 'clean' "$(GRADLE_DEFAULT_PARAMETERS)"

purge: clean
	$(GRADLEW) ':buildSrc:clean' "$(GRADLE_DEFAULT_PARAMETERS)"
ifeq ($(OS), Windows_NT)
	if exist $(MAKEFILE_PATH)\.gradle rmdir /s /q $(MAKEFILE_PATH)\.gradle
	if exist $(MAKEFILE_PATH)\buildSrc\.gradle rmdir /s /q $(MAKEFILE_PATH)\buildSrc\.gradle
	if exist $(MAKEFILE_PATH)\buildSrc\.kotlin rmdir /s /q $(MAKEFILE_PATH)\buildSrc\.kotlin
else
	rm -rf $(MAKEFILE_PATH)/.gradle
	rm -rf $(MAKEFILE_PATH)/buildSrc/.gradle
	rm -rf $(MAKEFILE_PATH)/buildSrc/.kotlin
endif

rebuild-build-logic:
	$(GRADLEW) ':buildSrc:clean' "$(GRADLE_DEFAULT_PARAMETERS)"
	$(GRADLEW) ':buildSrc:jar' "$(GRADLE_DEFAULT_PARAMETERS)"

compile:
	$(GRADLEW) 'classes' "$(GRADLE_DEFAULT_PARAMETERS)"

build:
	$(GRADLEW) -x "test" "build" "$(GRADLE_DEFAULT_PARAMETERS)"

install:
	$(GRADLEW) -x "test" "publishToMavenLocal" --no-parallel "$(GRADLE_DEFAULT_PARAMETERS)"

publish: install
	echo "警告：即将发布到Maven中央仓库！"
	read -p "确认继续？(yes/no) " confirm && [ $$confirm = "yes" ] || exit 1
	$(GRADLEW) -x "test" "publishAllPublicationsToMavenCentralRepository" --no-parallel "$(GRADLE_DEFAULT_PARAMETERS)"

test:
	$(GRADLEW) "test" "$(GRADLE_DEFAULT_PARAMETERS)"

wrapper:
	$(GRADLEW) ":wrapper" "$(GRADLE_DEFAULT_PARAMETERS)"

docker-build:
	$(GRADLEW) ':project-integration-test:jibDockerBuild' "$(GRADLE_DEFAULT_PARAMETERS)"

docker-remove-dangling:
	docker image prune -f

docker-purge:
	docker image prune -af
