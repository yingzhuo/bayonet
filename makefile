ifeq ($(OS), Windows_NT)
	MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew.bat
else
	MAKEFILE_PATH := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew
endif

.DEFAULT_GOAL := clean

.PHONY: clean purge rebuild-build-logic compile build install publish test

.SILENT:

clean:
	$(GRADLEW) 'clean' -q

purge: clean
	$(GRADLEW) ':buildSrc:clean' -q
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
	$(GRADLEW) ':buildSrc:clean' -q
	$(GRADLEW) ':buildSrc:jar' -q

compile:
	$(GRADLEW) 'classes'

build:
	$(GRADLEW) -x "test" "build"

install:
	$(GRADLEW) -x "test" "publishToMavenLocal" --no-parallel

publish: install
	echo "警告：即将发布到Maven中央仓库！"
	read -p "确认继续？(yes/no) " confirm && [ $$confirm = "yes" ] || exit 1
	$(GRADLEW) -x "test" "publishAllPublicationsToMavenCentralRepository" --no-parallel

test:
	$(GRADLEW) "test"
