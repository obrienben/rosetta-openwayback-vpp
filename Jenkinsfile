stage 'Prepare'
node {
	checkout scm
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
	sh 'curl -sS -O https://raw.githubusercontent.com/ExLibrisGroup/Rosetta.dps-sdk-projects/master/5.0.1/dps-sdk-deposit/lib/dps-sdk-5.0.1.jar'
  sh 'mvn install:install-file -Dfile=dps-sdk-5.0.1.jar -DgroupId=com.exlibris.dps -DartifactId=dps-sdk -Dversion=5.0.1 -Dpackaging=jar'
}


stage 'Build'
node {
	checkout scm
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
	sh 'mvn clean install'
}

stage 'Verify'
node {
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
	sh 'mvn verify'
}

stage 'Quality'
node {
	env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
        sh 'mvn -Dmaven.test.failure.ignore=false -P sonar sonar:sonar'
}
