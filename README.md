# ConQuest

## SonarQube MCP server

The repo ships a project-scoped MCP server (`.mcp.json`) that connects an AI agent to this
project's SonarQube Cloud data — issues, quality gate, security hotspots, rules, measures.
It is read-only: it reports what Sonar already knows, it does not run an analysis.

Requires Docker.

1. In SonarQube Cloud, go to **My Account → Access Tokens** and generate one on the
   **Personal Tokens** tab (a project analysis or scoped organization token will not work —
   the MCP server needs a user-scoped token).
2. Export the token and your organization key (the slug in
   `sonarcloud.io/organizations/<org-key>`, found under **My Account → Organizations**)
   in your shell profile — never in this repo:

   ```sh
   export SONARQUBE_TOKEN="squ_..."
   export SONARQUBE_ORG="your-org-key"
   ```

3. Restart your agent from a shell that has those variables, and approve the project MCP
   server when prompted. Verify with `claude mcp list` — `sonarqube` should show as connected.

`.mcp.json` only references the variables, so no secret is ever committed.
