pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Archive Dependencies List') {
            steps {
                sh './gradlew listDependencies'
                archiveArtifacts artifacts: 'build/reports/dependencies.csv', fingerprint: true
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'cfuser',
                usernameVariable: 'CF_CCUSER', passwordVariable: 'CF_CCPASSWORD']]) {
                   sh 'echo Pushing to Cloud Foundry'
                   sh './gradlew cf-push-blue-green'
                }
            }
        }
    }
}

/*
# Getting Started

## Setting Up CI
Your project includes a Jenkinsfile with a simple pipeline. It will run your tests and build your artifacts by default.
To setup your Jenkins instance to build this repository:

1) Create a new pipeline item

2) Set the jobs Build Trigger to poll SCM

3) Set the Pipeline Definition to `Pipeline script from SCM`

4) Set SCM to `git` and fill in your repository URL and credentials accordingly. 

For more information and examples, [click here](https://go.cloudbees.com/docs/cloudbees-documentation/use/automating-projects/jenkinsfile/
)

#### PCF Blue Green Deploys
Blue-Green deploys are provided by the [cf-plugin](https://github.ford.com/Quartermaster/ya-cf-app-gradle-plugin). 
To enable this, a few things need to be setup.  
First you'll need to update some fields in the `cfConfig` section of `build.gradle`.  
`ccHost` is your PCF API endpoint. You can find this by logging into the PCF App Manager under the Tools menu     
`space` is the name of the PCF Space you want to push the app to.   
`org` is the name of the PCF Organization you want to push the app to.   
`domain` is the domain where your application lives.  
Example images can be found [here](https://github.ford.com/Quartermaster/Quartermaster/tree/assets/README/cf-config)

A working example can be found in the [Quartermaster build.gradle](https://github.ford.com/Quartermaster/Quartermaster/blob/293fef277a09c8efd3e924a361e48957f5dd0c4a/build.gradle#L85)

For deployments from Jenkins to work, you'll have to do some setup. The Jenkinsfile is built to read from a Global Credentials,
which then injects the `CF_CCUSER` and `CF_CCPASSWORD` variables into the build environment.
To achieve this you'll need to add new Global Credentials to Jenkins of kind 'Username with Password' with an ID of 'cfUser'. 
Fill out the Username and Password fields with the appropriate information.

#### Multi-foundation Deployments
The plugin allows for overriding values via the command line. You can override any field to fit your multi-foundation deployments.
[See an example here.](https://github.ford.com/Quartermaster/Quartermaster/blob/b43cb851fb461be7b5ca754e4f9ed680061e819f/Jenkinsfile#L28-L54)


## Included Dependencies
Any extra documentation about included dependencies.  

*/
