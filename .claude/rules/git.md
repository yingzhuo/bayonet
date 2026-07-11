---
description: Git 操作规范（提交、分支、合并）
paths: "**/*"
---

# git操作规范

## 总原则

- **所有 git 操作必须经过用户确认**, 不得自动执行 commit、push、merge 等操作
- 提交前必须运行完整测试 (`make test`), 确保绿色通过
- 禁止对 `main` / `master` / `release/*` 分支直接 push, 必须通过 PR/MR

## 提交信息格式

采用 Conventional Commits 规范:

### type 取值

- `feat`: 新功能
- `fix`: Bug修复
- `refactor`: 重构（不新增功能也不修 bug）
- `test`: 增加测试
- `docs`: 文档变更
- `chore`: 构建/CI/工具变更
- `style`: 代码格式（不影响逻辑）
- `perf`: 性能优化

### scope 取值

- 模块名小写
- 跨模块改动可以不写 scope

### subject 要求

- 使用简体中文
- 祈使句
- 不超过72字符

### body (可选)

- 解释 Why 而非 What
- 关联 issue 编号：`Closes #123`

## 提交粒度

- **一个提交只做一件事**：不要混合 feat + fix + refactor
- 如果 Claude 在一次对话中生成了多个独立改动，应拆分为多个提交，而不是一个大提交
- 提交前先 `git diff --stat` 确认改动范围合理

## 分支命名

- 功能分支：`feat/<issue-number>-<short-desc>`（如 `feat/456-coupon`）
- Bug 修复分支：`fix/<issue-number>-<short-desc>`
- 禁止在 `main` 上直接开发，必须先切分支

## 合并策略

- 优先使用 **rebase** 而非 merge，保持历史线性
- 合并前确保分支基于最新的 `main` (`git rebase main`)
- 合并后删除远程分支 (`git push origin --delete <branch>`)

## 检查清单（Claude 每次 commit 前自查）

- 是否已运行测试并通过？
- 提交信息是否符合 Conventional Commits 格式？
- 本次改动是否只聚焦一个目的？
- 是否有未跟踪的敏感文件？
- 当前分支是否是 `main`？如果是，拒绝直接提交。

## 不要做什么

- 没有我的明确要求不要提交代码，更不要推送

## 重要提示

- **所有 git 操作（commit、push、merge、rebase 等）都必须经过我明确授权**，即使规则文件写了可以做，也必须先问过我
- 没有我的口头或书面命令，不要执行任何提交或推送操作
