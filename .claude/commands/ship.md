---
name: ship
description: 提交并推送所有代码到远程分支
---

# /ship

## 步骤

1. **检查当前分支**
    - 运行 `git branch --show-current`
    - 如果当前分支是 `main`、`master` 或以 `release/` 开头，提示并拒绝继续

2. **检查工作区状态**
    - 运行 `git status --short`
    - 如果没有改动，提示"当前工作区无改动，无需提交"并退出

3. **提交所有代码**
    - 运行 `git add -A`
    - 运行 `git commit`，使用 Conventional Commits 格式：
      ```
      <type>: <描述>
      ```
    - type 从 `feat` / `fix` / `refactor` / `docs` / `chore` / `style` / `test` / `perf` 中选择
    - 描述用简体中文祈使句，不超过 72 字符
    - 如果无法自动判断 type 和描述，向用户确认后再提交

4. **推送**
    - 运行 `git push origin <当前分支>`

5. **输出结果**
    - 告知用户提交 hash 和分支名称
