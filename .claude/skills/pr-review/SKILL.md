---
name: pr-review
description: Review a pull request against gto-support project conventions. Use when asked to review a PR, check code quality, or audit changes.
argument-hint: [pr-number]
allowed-tools: Bash, Read, Grep, Glob, Write, Edit
---

Review pull request $ARGUMENTS against the gto-support project conventions.

## Steps

1. Check for dismissed issues by reading `.claude/skills/pr-review/dismissed-issues.md` if it exists.
   Load all dismissed entries — each has a **Pattern** and **Reason**. You will use these to suppress matching findings later.

2. Fetch the PR diff and metadata. If `$ARGUMENTS` is provided, use it as the PR number:
```
gh pr diff $ARGUMENTS
gh pr view $ARGUMENTS
```
If no PR number is given (or the above fails because no upstream PR exists), fall back to reviewing the current branch against `master`:
```
git diff master...HEAD
git log master...HEAD --oneline
```
Use the branch name and commit log as the "title" in the review header.

3. Identify all changed files and categorize them (new module, existing module change, build logic, source set restructure, dependency update, etc.).

4. Run ktlint and record the result for the Code Style checklist:
```
./gradlew :build-logic:ktlintCheck ktlintCheck
```
A failure is reported as a ❌ Must Fix item in the review output — it does not stop the rest of the review.

5. Review each category using the checklist below.

6. Before outputting, cross-reference every finding against dismissed patterns. A finding matches a dismissed pattern when it describes the same class of issue (not necessarily the exact file/line — match by concept). Move matched findings to a separate suppressed list.

7. Output a structured review (format below).

8. After the review output, print:

```
---
To dismiss a finding so it won't appear in future reviews, say:
  dismiss: <short title> — <reason>
```

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
- [ ] `compileOnly` dependencies scoped correctly and not leaked to `commonMain`

### Dependency Management

- [ ] All dependency coordinates use `libs.*` aliases from `gradle/libs.versions.toml`
- [ ] New dependencies add entries to `libs.versions.toml` — no inline versions in `build.gradle.kts`
- [ ] New version catalog entries follow existing naming conventions (`libs.androidx.core`, `libs.kotlin.coroutines.test`, etc.)
- [ ] `api` vs `implementation` vs `compileOnly` scoping is appropriate:
  - `api` only for types exposed in the module's public API
  - `compileOnly` for optional integrations (caller must provide the dependency)

### Source Set Migrations (Android → KMP)

When an existing Android module is converted to KMP:
- [ ] Source files moved from `src/main/` → `src/androidMain/`
- [ ] Test files moved from `src/test/` → `src/androidUnitTest/`
- [ ] Instrumented test files moved from `src/androidTest/` → `src/androidInstrumentedTest/`
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

#### ⏭️ Suppressed
- <short title> — dismissed: <reason>
(omit this section entirely if nothing was suppressed)

### Overall Verdict
APPROVE / REQUEST CHANGES / COMMENT
<brief rationale>
```

Be specific. Reference file paths and line numbers. Cite the relevant convention from CLAUDE.md or this checklist when flagging an issue.

---

## Handling Dismissals

When the user says `dismiss: <title> — <reason>` (in any form — "dismiss the X issue because Y", etc.):

1. Read `.claude/skills/pr-review/dismissed-issues.md` if it exists (create it if not).
2. Run `git config user.name` to get the current user's name.
3. Append a new entry in this format:

```markdown
## <title>
**Pattern**: <describe the class of issue broadly enough to match future occurrences>
**Reason**: <reason the user gave>
**Dismissed**: <today's date as YYYY-MM-DD>
**Dismissed by**: <git user.name>
```

4. Confirm to the user what was added and that it will be suppressed in future reviews.
