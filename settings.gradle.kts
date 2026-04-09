pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()

        // 仅 GitHub 生态：避免与 google / mavenCentral 重复全量走镜像
        maven(url = uri("https://maven.aliyun.com/repository/public/")) {
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroupByRegex("io\\.github\\..*")
                // progressmanager 等未进 Maven Central，公库镜像仍可提供
                includeGroup("me.jessyan")
            }
        }
        maven(url = uri("https://jitpack.io")) {
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven(url = uri("https://maven.aliyun.com/repository/public/")) {
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroupByRegex("io\\.github\\..*")
                includeGroup("me.jessyan")
            }
        }
        maven(url = uri("https://jitpack.io")) {
            content {
                includeGroupByRegex("com\\.github\\..*")
            }
        }
    }
}

include(":app", ":common_core")
rootProject.name = "CommonLib"
