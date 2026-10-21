# GitHub Actions CI Integration

GitHub Actions allow building Continuous Integration pipeline for testing purposes.
GitHub Actions are visible through the `Actions` tab in a GitHub repository, are triggered automatically for each push or pull-request and run also in forks.
For further information about GitHub Actions, see the [official documentation](https://docs.github.com/en/free-pro-team@latest/actions).

## Caching

The GitHub Action [Cache](https://github.com/marketplace/actions/cache) allows reusing files between jobs and workflows.
The content of the cache cannot be cleaned on purpose, but is cleaned when the hash of the files used for the key changes.
This action is used in order to cache the maven dependencies.

## Docker images

The Docker images needed by the tests are built only once per workflow run, by the `build-docker` job.
The `saveDockerImages` action saves them in a single zstd-compressed archive and uploads it as a [workflow artifact](https://docs.github.com/en/actions/tutorials/store-and-share-data), kept for 1 day.
The test jobs load them with the `loadDockerImages` action instead of building them again.
When the artifact is not available anymore (e.g. "Re-run failed jobs" more than 1 day later) the test jobs build the images themselves and show a warning.
When also the Kapua artifacts of the run are not in the cache anymore, the jobs fail with an error asking to use "Re-run all jobs".

Workflow artifacts are used instead of the cache since they are scoped to the workflow run and do not count towards the 10 GB cache limit of the repository,
so concurrent workflow runs cannot evict each other's images (see [#3688](https://github.com/eclipse-kapua/kapua/issues/3688)).

# Composite Steps

Unfortunately, at the moment it is not possible to reuse some steps on multiple jobs.
This feature is a nice to have, since it would be possible to reuse the steps that are common to all the test jobs.
The feature has been already requested by the community, and it's being discussed here:
- https://github.community/t/reusing-sharing-inheriting-steps-between-jobs-declarations/16851
- https://github.com/actions/runner/issues/438

The GitHub Actions dev team has already introduced the composite run steps, see:
- https://github.com/actions/runner/blob/main/docs/adrs/0549-composite-run-steps.md
- https://docs.github.com/en/free-pro-team@latest/actions/creating-actions/creating-a-composite-run-steps-action

However, composite 'actions' are not supported yet, see:
- https://github.com/actions/runner/issues/646
- https://github.com/actions/runner/issues/646#issuecomment-734689627

# Re-run single jobs

It is currently not possible to restart single jobs.
For more information see the following issues on GitHub Actions Community that are tracking this feature request:
- https://github.community/t/ability-to-rerun-just-a-single-job-in-a-workflow/17234
- https://github.community/t/re-run-jobs/16145/

To cope with this missing feature, the [Retry](https://github.com/marketplace/actions/retry-step) GitHub Action is used.
This action allows retrying a failed step for a fixed amount of attempts.
