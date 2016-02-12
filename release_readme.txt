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
            <gpg.executable>c:\bin\GNU\GnuPG\gpg2.exe</gpg.executable>
            <gpg.keyname>use your own eg. A85A1122</gpg.keyname>
            <gpg.passphrase>use your own</gpg.passphrase>
          </properties>
        </profile>
    </profiles>

    <servers>
      <server>
        <id>ossrh</id>
        <username>use your own</username>
        <password>use your own</password>
      </server>
    </servers>

</settings>


After having the project ready for release with all tests passed, 

(1) remove suffix -SNAPSHOT from project version by running:
mvn versions:set -DnewVersion=0.1  (version with -SNAPSHOT removed)

(2) Commit and push the release to git, possibli with a tag denoting the release.

(3)Run maven deploy from Idea or console via:
mvn clean deploy

(4) make new working project version by increasing number and appending -SNAPSHOT:
mvn versions:set -DnewVersion=0.2-SNAPSHOT

(5) Commit and push.








