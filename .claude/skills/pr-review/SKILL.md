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

4. Run ktlint and lint, recording results for the Code Style checklist:
```
./gradlew :build-logic:ktlintCheck ktlintCheck
./gradlew lint
```
Failures are reported as ❌ Must Fix items in the review output — they do not stop the rest of the review.

5. Review each category using the checklist below.

6. Before outputting, cross-reference every finding against dismissed patterns. A finding matches a dismissed pattern when it describes the same class of issue (not necessarily the exact file/line — match by concept). Move matched findings to a separate suppressed list.

7. Output a structured review (format below).

8. Post inline comments to the PR for every ⚠️ and ❌ finding that references a specific file and line number. **Skip this step entirely when reviewing a branch with no PR — there is nowhere to post.** Before posting, deduplicate against all existing comments (resolved or not) to avoid re-posting anything already raised:

```bash
# Get the head SHA, repo, and all existing review comments (resolved and unresolved)
HEAD_SHA=$(gh pr view $ARGUMENTS --json headRefOid -q .headRefOid)
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
EXISTING=$(gh api repos/$REPO/pulls/$ARGUMENTS/comments --jq '[.[] | select(.in_reply_to_id == null) | {path:.path, line:.line, body:.body}]')
```

For each finding, check whether any existing comment (resolved or not) already covers the same file + line (or contains substantially the same text). Skip any finding that is already covered. Then bundle the remaining new comments into a single review submission:

```bash
gh api repos/$REPO/pulls/$ARGUMENTS/reviews \
  --method POST \
  --field commit_id="$HEAD_SHA" \
  --field event="COMMENT" \
  --field "comments[][path]=<file path>" \
  --field "comments[][line]=<line number>" \
  --field "comments[][side]=RIGHT" \
  --field "comments[][body]=<finding text>

🤖 Posted by [Claude Code](https://claude.ai/code)" \
  # repeat --field "comments[]..." for each new finding
```

Use the exact file path from the diff and the line number in the current version of the file (RIGHT side). Each comment body should contain the full finding description. Always append the attribution footer `\n\n🤖 Posted by [Claude Code](https://claude.ai/code)` to each comment. If no new actionable findings exist (only ✅ items or all already commented), skip this step.

9. If the review has **no ❌ or ⚠️ findings** (only ✅ and/or ⏭️ items), ask the user whether to post the full review. **Skip this step entirely when reviewing a branch with no PR — branch review mode is local-only.** Otherwise, if they say yes:
   - Check whether the PR author matches the current git user (`gh pr view $ARGUMENTS --json author -q .author.login` vs `gh api user -q .login`)
   - If it is a **self-review**, post with `--comment` (GitHub does not allow self-approval)
   - If it is **someone else's PR**, ask whether to approve or just comment, then post with `--approve` or `--comment` accordingly
   - Always append `\n\n🤖 Posted by [Claude Code](https://claude.ai/code)` to the body

10. After the review output, print:

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
- [ ] Compose-enabled module applies `compose-conventions` (Android) or `compose-multiplatform-conventions` (KMP) — not a manual `buildFeatures.compose = true` plus hand-added compose runtime/debug/test dependencies

### Kotlin Multiplatform (KMP) Modules

- [ ] iOS targets added via `configureIosTarget()` (not manually declared)
- [ ] JS target added via `configureJsTarget()` if needed
- [ ] JVM target added via `configureJvmTarget()` if needed
- [ ] Source sets use KMP layout v2 names: `androidMain`, `androidHostTest`, `androidDeviceTest`, `commonMain`, `commonTest`, `iosMain`, etc. (the `com.android.kotlin.multiplatform.library` plugin uses `androidHostTest`/`androidDeviceTest`, not `androidUnitTest`/`androidInstrumentedTest`)
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
- [ ] Annotation processors use KSP (`ksp(...)` + `alias(libs.plugins.ksp)`), not kapt. `legacy.kapt` (`com.android.legacy-kapt`) is reserved for AGP databinding / view-binding modules that KSP can't handle — not for Dagger/Hilt/Moshi codegen

### Source Set Migrations (Android → KMP)

When an existing Android module is converted to KMP:
- [ ] Source files moved from `src/main/` → `src/androidMain/`
- [ ] Test files moved from `src/test/` → `src/androidHostTest/`
- [ ] Instrumented test files moved from `src/androidTest/` → `src/androidDeviceTest/`
- [ ] Old `src/main/` and `src/test/` directories fully removed
- [ ] `dependencies { }` block replaced with `kotlin { sourceSets { androidMain { dependencies { } } } }`
- [ ] `android.namespace` kept (now set inside the `kotlin { android { namespace = … } }` block)

### Build Logic (`build-logic/`)

Changes to convention plugins or configuration files affect every module — review carefully:
- [ ] No SDK version changes without intentional justification (compileSdk 37, minSdk 23)
- [ ] JVM toolchain version not changed inadvertently (Java 17)
- [ ] Publishing configuration changes don't break group IDs (`org.ccci.gto.android` / `org.ccci.gto.android.testing`)
- [ ] Test heap or sharding configuration changes are intentional

### Code Style

Ktlint and `.editorconfig` enforce most style rules (line length, final newline, unused imports, formatter rules) — step 4's pre-flight already covers those. Manual checks:

- [ ] No trailing comma on single-line function/constructor signatures or calls (ktlint's `trailing-comma-on-*` rules are disabled in `.editorconfig`, so the pre-flight does NOT catch this)
- [ ] `internal` used appropriately — avoid over-exposing API surface

### Testing

- [ ] Flow-based tests use Turbine (`flow.test { … }`)
- [ ] Tests live in the correct source set — `commonTest` for shared logic, `androidHostTest` for Android/Robolectric tests

### General Quality

- [ ] Deprecated modules/APIs include `@Deprecated` annotations with a since-version and migration hint
- [ ] No Android framework types (`Context`, `Activity`, etc.) leaked into `commonMain` source sets
- [ ] No hardcoded strings that should be version catalog entries
- [ ] Module interdependencies use `project(":module-name")` — no external coordinate references to sibling modules

### PR Hygiene

- [ ] No unrelated auto-formatter whitespace churn mixed into the diff (check `git diff master...HEAD --stat` — flag files whose churn doesn't match the stated PR scope)

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

4. If the current session reviewed a PR, find any open (unresolved) comment thread on that PR matching the dismissed issue. Use the GraphQL API to locate threads and resolve the matching one, replying with the dismissal reason first:

```bash
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
OWNER=${REPO%%/*}
REPONAME=${REPO##*/}

# Find unresolved review threads
gh api graphql -f query="
{
  repository(owner: \"$OWNER\", name: \"$REPONAME\") {
    pullRequest(number: $PR_NUMBER) {
      reviewThreads(first: 100) {
        nodes {
          id
          isResolved
          comments(first: 1) {
            nodes { id body path line }
          }
        }
      }
    }
  }
}"
```

Match the thread by file path, line number, or substantial text overlap with the dismissed finding. Then reply to the thread and resolve it:

```bash
# Reply to the thread's first comment explaining the dismissal
gh api repos/$REPO/pulls/$PR_NUMBER/comments \
  --method POST \
  --field in_reply_to=<comment_id> \
  --field body="Dismissed: <reason given by user>

🤖 [Claude Code](https://claude.ai/code)"

# Resolve the thread via GraphQL
gh api graphql -f query="
mutation {
  resolveReviewThread(input: { threadId: \"<thread_node_id>\" }) {
    thread { id isResolved }
  }
}"
```

5. Confirm to the user what was added and that it will be suppressed in future reviews.
