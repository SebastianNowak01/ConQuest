---
name: sonar-triage
description: Pull ConQuest's SonarQube Cloud findings through the project MCP server and work through them. Use when asked about sonar issues, code smells, the quality gate, security hotspots, coverage, or "clean up before the PR".
---

# SonarQube triage for ConQuest

The repo ships a project-scoped MCP server in `.mcp.json` (`sonarqube`, runs in Docker). It is
**read-only**: it reports what Sonar already analysed on a pushed branch or PR. It does not run an
analysis, so local uncommitted work is invisible to it.

## Before starting

The server needs `SONARQUBE_TOKEN` (a *personal* token — project/org tokens are rejected) and
`SONARQUBE_ORG` in the shell that launched the agent; see `README.md`. If tools error on auth,
that is the cause — tell the user rather than retrying.

## Resolving the project

No `sonar.projectKey` is committed anywhere in this repo, so **always** start with
`search_my_sonarqube_projects` and use the key it returns. A wrong key silently returns another
project's findings.

## Branch vs PR

- Working on a PR → `list_pull_requests`, then pass its key as `pullRequest`.
- Working on a branch → `list_branches`, then pass the name as `branch`.
- Never pass both on one call. Omit both to query `main`.
- A git branch name is never a valid `pullRequest` value.

## Useful calls

- `get_project_quality_gate_status` — start here; it says whether the gate is failing and why.
- `search_sonar_issues_in_projects` — the findings themselves. Filter by impact/severity and take
  the blockers first.
- `search_security_hotspots` / `show_security_hotspot` — hotspots are review items, not
  necessarily bugs.
- `show_rule` — read the rule before "fixing" a finding you do not understand.
- `get_component_measures`, `get_file_coverage_details`, `search_files_by_coverage` — coverage.
- `search_duplicated_files`, `get_duplications` — duplication.
- `analyze_code_snippet` — check a snippet before committing it.

## Working the list

1. Get the gate status, then the issue list for the right branch/PR.
2. Group findings by rule — they usually cluster, and one pattern fix clears several.
3. Fix them in the source, following `compose-screen` / `conquest-viewmodel` conventions. Many
   Sonar findings overlap with detekt, so `verify-changes` catches the fix locally.
4. Run the `verify-changes` gate. Sonar only updates after the branch is pushed and CI analyses
   it — do not claim an issue is resolved on Sonar's side until a new analysis exists.

Changing an issue's status (`change_sonar_issue_status`, `change_security_hotspot_status`) marks
it resolved/false-positive/accepted **on the server for everyone**. Only do it when the user
explicitly asks, never to make a count go down.
