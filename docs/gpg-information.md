## GPG 信息

| 电子邮件           | 公钥ID           | 公钥指纹                                 |
|--------------------|------------------|------------------------------------------|
| yingzhor@gmail.com | 6B11FB7FE9ECA55D | 3825E69D2277CFA095F9AA456B11FB7FE9ECA55D |

### 获取公钥

```bash
gpg --recv-keys 6B11FB7FE9ECA55D
```

### 验证指纹

```bash
gpg --verify bayonet-bom-4.1.0.pom.asc bayonet-bom-4.1.0.pom
```

你可以比对公钥指纹确保安全

```text
gpg: Signature made Mon Jul 27 21:12:36 2026 CST
gpg:                using RSA key 6B11FB7FE9ECA55D
gpg: Good signature from "yingzhuo <yingzhor@gmail.com>" [unknown]
gpg: WARNING: This key is not certified with a trusted signature!
gpg:          There is no indication that the signature belongs to the owner.
      3825E69D2277CFA095F9AA456B11FB7FE9ECA55D
```
