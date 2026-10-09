# Publishing

The GitHub Actions workflow builds every pull request and `main` push. Pushing a
tag matching `v*` builds `FemaleGenderPlugin.jar`, creates a GitHub Release and
publishes that exact file to Modrinth.

## One-time GitHub setup

Add these **Actions secrets** to `TheReizi/Female-Gender-Plugin`:

| Secret | Value |
| --- | --- |
| `MODRINTH_PROJECT_ID` | The ID or slug of the already-created Modrinth project. |
| `MODRINTH_TOKEN` | A Modrinth personal access token permitted to create versions for that project. |

Use a release tag such as `v1.0.1`:

```powershell
git tag v1.0.1
git push origin v1.0.1
```

The workflow's release JAR embeds the tag as the Paper plugin version.

## SpigotMC

SpigotMC has no supported API for uploading a resource file or creating a
resource update. Its public Spiget API is read-only. Consequently, a supported
CI/CD pipeline cannot publish a JAR to SpigotMC automatically. The GitHub
Release produced by the workflow is the release artifact to attach to the
existing SpigotMC resource manually.

Once a SpigotMC resource exists, add its release link to the project page and
use the GitHub Release asset for each update. Browser/session-cookie automation
is deliberately not included because it is unsupported by SpigotMC and tends to
break whenever their XenForo forms change.
