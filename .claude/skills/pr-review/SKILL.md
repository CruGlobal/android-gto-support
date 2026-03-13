---
name: pr-review
description: Review a pull request against gto-support project conventions. Use when asked to review a PR, check code quality, or audit changes.
argument-hint: [pr-number]
allowed-tools: Bash, Read, Grep, Glob
---

Review pull request $ARGUMENTS against the gto-support project conventions.

## Steps

1. Fetch the PR diff and metadata:
```
gh pr diff $ARGUMENTS
gh pr view $ARGUMENTS
```

2. Identify all changed files and categorize them (new module, existing module change, build logic, source set restructure, dependency update, etc.).

3. Review each category using the checklist below.

4. Output a structured review.

---

## Review Checklist

### Module Structure

**New modules**
- [ ] Module added to `settings.gradle.kts`
- [ ] Uses the correct convention plugin for its type (see below)
- [ ] `android.namespace` set (for Android or KMP-with-Android modules)
- [ ] All dependencies declared via `libs.*` version catalog aliases — no hardcoded versions
- [ ] Consumer ProGuard rules added if the module exposes non-trivial public API (via `proguard-consumer.pro`)

**Convention plugin selection**
- [ ] Pure Android library → `gto-support.android-conventions`
- [ ] KMP with Android + iOS/JS/JVM → `gto-support.multiplatform-android-conventions`
- [ ] KMP without Android → `gto-support.multiplatform-conventions`
- [ ] Java/JVM-only → `gto-support.java-conventions`
- [ ] Android test utility → `gto-support.android-testing-conventions`

### Kotlin Multiplatform (KMP) Modules

- [ ] iOS targets added via `configureIosTarget()` (not manually declared)
- [ ] JS target added via `configureJsTarget()` if needed
- [ ] JVM target added via `configureJvmTarget()` if needed
- [ ] Source sets use KMP layout v2 names: `androidMain`, `androidUnitTest`, `androidInstrumentedTest`, `commonMain`, `commonTest`, `iosMain`, etc.
- [ ] Android-specific code (framework APIs, `Context`, `android.*`, etc.) lives in `androidMain` — not `commonMain`
- [ ] Dependencies declared in the most appropriate source set (not all dumped into `androidMain` if they belong in `commonMain`)
- [ ] `compileOnly` dependencies (e.g. `swiperefreshlayout`) scoped correctly and not leaked to `commonMain`

### Dependency Management

- [ ] All dependency coordinates use `libs.*` aliases from `gradle/libs.versions.toml`
- [ ] New dependencies add entries to `libs.versions.toml` — no inline versions in `build.gradle.kts`
- [ ] New version catalog entries follow existing naming conventions (`libs.androidx.core`, `libs.kotlin.coroutines.test`, etc.)
- [ ] `api` vs `implementation` vs `compileOnly` scoping is appropriate:
  - `api` only for types exposed in the module's public API
  - `compileOnly` for optional integrations (caller must provide the dependency)

### Source Set Migrations (Android → KMP)

When an existing Android module is converted to KMP:
- [ ] Source files moved from `src/main/java/` → `src/androidMain/java/`
- [ ] Test files moved from `src/test/java/` → `src/androidUnitTest/java/`
- [ ] Instrumented test files moved from `src/androidTest/java/` → `src/androidInstrumentedTest/java/`
- [ ] Old `src/main/` and `src/test/` directories fully removed
- [ ] `dependencies { }` block replaced with `kotlin { sourceSets { androidMain { dependencies { } } } }`
- [ ] `android.namespace` kept (now set inside `android { }` block)

### Build Logic (`build-logic/`)

Changes to convention plugins or configuration files affect every module — review carefully:
- [ ] No SDK version changes without intentional justification (compileSdk 36, minSdk 23, targetSdk 36)
- [ ] JVM toolchain version not changed inadvertently (Java 17)
- [ ] Publishing configuration changes don't break group IDs (`org.ccci.gto.android` / `org.ccci.gto.android.testing`)
- [ ] Test heap or sharding configuration changes are intentional

### Code Style

- [ ] Kotlin files pass ktlint (run `:ktlintCheck`)
- [ ] Java files pass Checkstyle
- [ ] Files end with a trailing newline
- [ ] No unused imports
- [ ] `internal` used appropriately — avoid over-exposing API surface

### General Quality

- [ ] Deprecated modules/APIs include `@Deprecated` annotations with a since-version and migration hint
- [ ] No Android framework types (`Context`, `Activity`, etc.) leaked into `commonMain` source sets
- [ ] No hardcoded strings that should be version catalog entries
- [ ] Module interdependencies use `project(":module-name")` — no external coordinate references to sibling modules

---

## Output Format

Structure the review as:

```
## PR Review: <title> (#<number>)

### Summary
<1–2 sentence summary of what the PR does>

### Checklist Findings

#### ✅ Looks Good
- <item>

#### ⚠️ Minor Issues
- <file:line> — <issue> — <suggested fix>

#### ❌ Must Fix
- <file:line> — <issue> — <suggested fix>

### Overall Verdict
APPROVE / REQUEST CHANGES / COMMENT
<brief rationale>
```

Be specific. Reference file paths and line numbers. Cite the relevant convention from CLAUDE.md or this checklist when flagging an issue.
