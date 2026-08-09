---
name: ship
description: 提交并推送当前分支的全部改动到远程仓库。当用户说 "ship"、"提交并推送"、"发布当前改动"，或明确要求把本次改动 commit + push 时使用。
---

# Ship

将当前分支的全部改动提交并推送到远程仓库。调用本 skill 即视为用户发起提交请求，但 git 操作仍需遵守仓库 `AGENTS.md` 的 Git 规范，提交前必须获得用户明确确认。

## 步骤

1. **检查分支**
   - 运行 `git branch --show-current`
   - 若分支为 `main`、`master` 或以 `release/` 开头，拒绝继续并说明原因

2. **检查工作区**
   - 运行 `git status --short`
   - 若无改动，提示"当前工作区无改动，无需提交"并结束

3. **运行测试**
   - 运行 `make test`
   - 测试失败则停止，报告失败原因，不继续提交

4. **审查改动范围**
   - 运行 `git diff --stat` 确认改动范围合理
   - 若存在未跟踪的敏感文件（`secrets/`、`.env`、`*.key` 等），提示用户并暂停

5. **暂存并确认提交信息**
   - 运行 `git add -A`
   - 按 Conventional Commits 生成提交信息，展示给用户，获得明确确认后执行 `git commit`

6. **推送**
   - 运行 `git push origin <当前分支>`

7. **汇报**
   - 告知用户提交 hash 和分支名

## 提交信息规范

- 格式：`<type>: <描述>`
- type 从 `feat` / `fix` / `refactor` / `docs` / `chore` / `style` / `test` / `perf` 中选择
- 描述用简体中文祈使句，不超过 72 字符
- 若无法自动判断 type 和描述，向用户确认后再提交
