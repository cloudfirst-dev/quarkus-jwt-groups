#!groovy
node('maven') {
  // Define Maven Command to point to the correct
  // settings for our Nexus installation
  def mvnCmd = "mvn"

  // Checkout Source Code.
  stage('Checkout Source') {
    checkout scm
  }
	

  // Using Maven build the jar files
  // Do not run tests in this step
  stage('Build jar') {
    echo "Building version ${project.version}"
    sh "${mvnCmd} clean package -DskipTests"
  }

  // Using Maven run the unit tests
  stage('Unit Tests') {
    echo "Running Unit Tests"
    sh "${mvnCmd} test"
  }

  // Using Maven to call SonarQube for Code Analysis
  //stage('Code Analysis') {
  //  echo "Running Code Analysis"
  //  sh "${mvnCmd} sonar:sonar -Dsonar.host.url=https://sonar.idsysapps.com -Dsonar.projectName=${JOB_BASE_NAME}-${devTag}"
  //}

  // Publish the built war file to Nexus
  stage('Publish to Nexus') {
    echo "Publish to Nexus"
    sh "${mvnCmd} deploy -DskipTests=true -DaltDeploymentRepository=nexus::default::http://nexus.nexus.svc.cluster.local:8081/repository/idm-snapshot"
  }
}
