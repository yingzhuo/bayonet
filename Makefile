ifeq ($(OS), Windows_NT)
	MAKEFILE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew.bat
else
	MAKEFILE_PATH := $(shell dirname $(realpath $(firstword $(MAKEFILE_LIST))))
	GRADLEW := $(MAKEFILE_PATH)/gradlew
endif

.DEFAULT_GOAL := clean

.PHONY: clean purge rebuild-build-logic compile build install publish update-gradle-wrapper test check

.SILENT:

clean:
	$(GRADLEW) 'clean' -q

purge: clean
	$(GRADLEW) ':build-logic:clean' -q
ifeq ($(OS), Windows_NT)
	if exist $(MAKEFILE_PATH)\.gradle rmdir /s /q $(MAKEFILE_PATH)\.gradle
	if exist $(MAKEFILE_PATH)\build-logic\.gradle rmdir /s /q $(MAKEFILE_PATH)\build-logic\.gradle
else
	rm -rf $(MAKEFILE_PATH)/.gradle
	rm -rf $(MAKEFILE_PATH)/build-logic/.gradle
endif

rebuild-build-logic:
	$(GRADLEW) ':build-logic:clean' -q
	$(GRADLEW) ':build-logic:jar' -q

compile:
	$(GRADLEW) 'classes'

build:
	$(GRADLEW) -x "check" -x "test" "build"

install:
	$(GRADLEW) -x "test" -x "check" "publishToMavenLocal" --no-parallel

publish: install
	echo "警告：即将发布到Maven中央仓库！"
	read -p "确认继续？(yes/no) " confirm && [ $$confirm = "yes" ] || exit 1
	$(GRADLEW) -x "test" -x "check" "publishToMavenCentralPortal" --no-parallel

update-gradle-wrapper:
	$(GRADLEW) ":wrapper" -q

test:
	$(GRADLEW) "test"

check:
	$(GRADLEW) "check"
