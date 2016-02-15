MAKING A RELEASE TO MAVEN CENTRAL REPO


setup pgp on local computer: http://central.sonatype.org/pages/working-with-pgp-signatures.html
and make sure the below xml is in the .m2/settings.xml file:

<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <profiles>
        <profile>
          <id>ossrh</id>
          <activation>
            <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
            <gpg.executable>user your own eg. c:\bin\GNU\GnuPG\gpg2.exe</gpg.executable>
            <gpg.keyname>use your own eg. A85A1122</gpg.keyname>
            <gpg.passphrase>use your own</gpg.passphrase>
          </properties>
        </profile>
    </profiles>

    <servers>
      <server>
        <id>ossrh</id>
        <username>use your own or saxorut</username>
        <password>use your own or 596...</password>
      </server>
    </servers>

</settings>


After having the project ready for release with all tests passed, 

(1) remove suffix -SNAPSHOT from project version by running at project root directory:
mvn versions:set -DnewVersion=0.1  (version with -SNAPSHOT removed)

(2) Commit and push the release to git, possibly with a tag denoting the release.

(3)Run maven deploy from Idea or console via:
mvn clean deploy

(4) set new working project version by increasing number and appending -SNAPSHOT:
mvn versions:set -DnewVersion=0.2-SNAPSHOT

(5) Commit and push.



The link to repository manager:  https://oss.sonatype.org/index.html#nexus-search;quick~latinolib
for login use saxorut with 59...

The Jira ticket: https://issues.sonatype.org/browse/OSSRH-20556


After deploy/release there will be the following error message, which is due to autoReleaseAfterClose being set to true, and is to be ignored:

[ERROR] Failed to execute goal org.sonatype.plugins:nexus-staging-maven-plugin:1.6.3:release (deploy-to-sonatype) on project latino4j-core: Execution deploy-to-sonatype of goal org.sonatype.plugins:nexus-staging-maven-plugin:1.6.3:release failed: Internal Server Error : entity body dump follows: <nexus-error>
[ERROR] <errors>
[ERROR] <error>
[ERROR] <id>*</id>
[ERROR] <msg>Unhandled: Missing staging repository: orglatinolib-1001</msg>
[ERROR] </error>
[ERROR] </errors>
[ERROR] </nexus-error>
[ERROR] -> [Help 1]


