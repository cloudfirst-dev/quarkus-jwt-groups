podTemplate(
  label: "maven-pod",
  cloud: "openshift",
  inheritFrom: "maven",
  containers: [
    containerTemplate(
      name: "jnlp",
      image: "quay.io/cloudfirst/jenkins-agent-maven-35-centos7:latest",
    )
  ],
  volumes: [
    secretVolume(
      mountPath: "/home/jenkins/.m2",
      secretName: "maven"
  ]
) {
	node('maven-pod') {
	  // Define Maven Command to point to the correct
	  // settings for our Nexus installation
	  def mvnCmd = "mvn"
	  def pom
	
	  // Checkout Source Code.
	  stage('Checkout Source') {
	    checkout scm
	  }
	  
	  stage('Get project version') {
	    pom = readMavenPom file: 'pom.xml'
	  }
		
	
	  // Using Maven build the jar files
	  // Do not run tests in this step
	  stage('Build jar') {
	    echo "Building version ${pom.version}"
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
	    sh "${mvnCmd} deploy -DskipTests=true"
	  }
	}
}
