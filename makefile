ifeq ($(OS), Windows_NT)
	MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew.bat
else
	MAKEFILE_PATH := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew
endif

.DEFAULT_GOAL := clean

.PHONY: clean purge rebuild-build-logic compile build install publish test wrapper

.SILENT:

clean:
	$(GRADLEW) 'clean' --console=plain

purge: clean
	$(GRADLEW) ':buildSrc:clean' --console=plain
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
	$(GRADLEW) ':buildSrc:clean' --console=plain
	$(GRADLEW) ':buildSrc:jar' --console=plain

compile:
	$(GRADLEW) 'classes' --console=plain

build:
	$(GRADLEW) -x "test" "build" --console=plain

install:
	$(GRADLEW) -x "test" "publishToMavenLocal" --no-parallel --console=plain

publish: install
	echo "警告：即将发布到Maven中央仓库！"
	read -p "确认继续？(yes/no) " confirm && [ $$confirm = "yes" ] || exit 1
	$(GRADLEW) -x "test" "publishAllPublicationsToMavenCentralRepository" --no-parallel --console=plain

test:
	$(GRADLEW) "test" --console=plain

wrapper:
	$(GRADLEW) ":wrapper" --console=plain
