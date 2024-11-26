pipeline {
    agent {
        node {
            label 'master'
        }
    }

    environment {
        APP_NAME = 'card-binding'
        APP_GROUP = 'Online Payment'
        HARBOR_NON_PROD_REPO_URL = 'jtl-tkgiharbor.hq.bni.co.id'
        HARBOR_NON_PROD_PROJECT_NAME = 'psd'
        HARBOR_PROD_REPO_URL = 'jkt-tkgiharbor.hq.bni.co.id'
        HARBOR_PROD_PROJECT_NAME = 'psd'
        HARBOR_CREDENTIAL_ID='dc89a5e1-2fef-44fc-849d-83e397575963'
        SONAR_LOGIN_ = 'squ_5aefb05efe590882a646a62002f19f252682d21e'
        SONAR_HOST_URL_ = 'http://10.63.97.5:9003'
        SONAR_HOME = tool 'SonarLatest'
        HTTP_PROXY  = '192.168.45.105:8080'
        HTTPS_PROXY = '192.168.45.105:8080'
        NO_PROXY    = '*.bni.co.id'
        APP_VERSION='1.0.1'
    }

    parameters {
        string(name: 'HARBOR_CREDENTIAL_ID', defaultValue: 'dc89a5e1-2fef-44fc-849d-83e397575963', description: 'Harbor Credential ID, Default is JTL')
        string(name: 'OCP_CLUSTER_API', defaultValue: 'https://api.dev-bpsoa.ocp.hq.bni.co.id:6443', description: 'OCP Cluster API')
        string(name: 'OCP_PROJECT_NAME', defaultValue: 'psd', description: 'Namespace OCP')
        string(name: 'OCP_CREDENTIAL_ID', defaultValue: 'b4a2c3f5-f720-463c-91ae-c52dd3a9d67a', description: 'OCP Credential ID')
    }

    options {
        disableConcurrentBuilds()
        timeout(time: 1, unit: 'HOURS')
    }

    stages {
        stage('Checking Files') {
            when {
                expression {
                    return env.BRANCH_NAME == 'sit' || env.BRANCH_NAME == 'development'
                }
            }
            steps {
                script {
                    try {
                        // Define the expected file structure
                        def expectedFolders = ["database", "doc", "source"]
                        def expectedFiles = ["Jenkinsfile","*.ocp.yaml", "README.md", "database/${APP_VERSION}/*.sql", "doc/${APP_VERSION}/*.postman_collection.json", "doc/${APP_VERSION}/*.docx"]

                        // Function to check if a folder exists
                        def checkFolder = { folder ->
                            if (fileExists(folder)) {
                                echo "Folder $folder ✅"
                            } else {
                                error "Folder $folder ❌"
                            }
                        }

                        // Function to check if a file exists
                        def checkFile = { filePattern ->
                            def filesExist = findFiles(glob: "**/${filePattern}")
                            if (filesExist) {
                                echo "Files for pattern $filePattern ✅"
                                filesExist.each { file ->
                                    echo " - $file"
                                }
                            } else {
                                error "No files found for pattern $filePattern ❌"
                            }
                        }

                        // Check for folders
                        expectedFolders.each { folder ->
                            checkFolder(folder)
                        }

                        // Loop through each file pattern and check if any file matches
                        expectedFiles.each { filePattern ->
                            checkFile(filePattern)
                        }
                    } catch (Exception e) {
                        error "Checking Files failed: ${e.message}"
                    }
                }
            }
        }

        stage('Check Memory set in yaml') {
         when {
             branch 'fixed-auth'
          }
                steps {
                    script {
                        try {
                            def expectedTexts = ["resources", "limits", "memory"]

                            def checkText = {textCheck ->
                            def grepOutput = sh(script: "cat ${APP_NAME}.ocp.yaml | grep -e '${textCheck}'", returnStatus: true)
                                if (grepOutput == 0) {
                                    echo "Spesifikasi ${textCheck} ditemukan. Melanjutkan ke tahap berikutnya... ✅"
                                } else {
                                    echo "Spesifikasi ${textCheck} tidak ditemukan. Keluar tanpa melanjutkan tahap berikutnya. ❌"
                                    error('Spesifikasi tidak ditemukan')
                                }
                            }

                            expectedTexts.each { text ->
                                    checkText(text)}
                        } catch (Exception e) {
                                error "Checking Files failed: ${e.message}"
                        }

                    }

                }
            }

//         stage('SonarQube Code Analysis') {
//           when {
//             branch 'fixed-auth'
//           }
//                 steps {
//                     script {
//                         def mvnHome = tool 'maven386'
//                         def scannerHome = tool 'SonarLatest'
//                         sh "cd source && ${mvnHome}/bin/mvn clean verify sonar:sonar -s settings.xml\
//                             -Dsonar.projectKey=psd-${APP_NAME} \
//                             -Dsonar.projectName=psd-${APP_NAME} \
//                             -Dsonar.projectVersion=${APP_VERSION} \
//                             -Dsonar.token=${SONAR_LOGIN_} \
//                             -Dsonar.host.url=${SONAR_HOST_URL_} \
//                             -DskipTests"
//                     }
//                     timeout(time: 2, unit: 'MINUTES') {
//                             sleep(time: 25, unit: 'SECONDS')
//                             script {
//                                 def qg = waitForQualityGate()
//                                 if (qg.status != 'OK') {
//                                     error "Pipeline aborted due to quality gate failure: ${qg.status}"
//                                 } else {
//                                     echo "Quality Success"
//                                 }
//                             }
//                         }
//                 }
//         }


        stage('Build and Deploy') {
            when {
                expression {
                    return env.BRANCH_NAME == 'fixed-auth' || env.BRANCH_NAME == 'development'
                }
            }
            steps {
                script {
                    echo " Building and Deploying for branch: ${env.BRANCH_NAME} "
                    sh "echo '----------- Build -----------'"
                    if (env.BRANCH_NAME == 'fixed-auth') {
                        sh """
                            cd source
                            docker build -f Dockerfile -t ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION} .
                           """
                    }

                    sh "echo '----------- Login Harbor -----------'"

                    withCredentials([usernamePassword(credentialsId: "${env.HARBOR_CREDENTIAL_ID}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "docker login -u $USERNAME -p $PASSWORD ${HARBOR_NON_PROD_REPO_URL}"

                        if (env.BRANCH_NAME == 'fixed-auth') {
                            sh "docker push ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION}"
                        } else if (env.BRANCH_NAME == 'development') {
                            sh "docker pull ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION}"
                            sh "docker tag ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION} ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}-qas/${APP_NAME}:${APP_VERSION}"
                            sh "docker push ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}-qas/${APP_NAME}:${APP_VERSION}"
                        }
                    }

                    sh "echo '----------- Login OCP -----------'"

                    withCredentials([usernamePassword(credentialsId: "${OCP_CREDENTIAL_ID}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "oc login ${OCP_CLUSTER_API} --username=$USERNAME --password=$PASSWORD --insecure-skip-tls-verify"
                        sh "oc project ${OCP_PROJECT_NAME}"
                        sh "oc delete -f ${APP_NAME}.ocp.yaml --ignore-not-found"
                        sh "oc apply -f ${APP_NAME}.ocp.yaml"
                    }

                    sh "echo '----------- Clean Up -----------'"
                    sh "docker image rm -f ${HARBOR_NON_PROD_REPO_URL}/${HARBOR_NON_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION} || true"
                    sh "docker image rm -f ${HARBOR_PROD_REPO_URL}/${HARBOR_PROD_PROJECT_NAME}/${APP_NAME}:${APP_VERSION} || true"
                }
            }
        }
    }

    post {
        success {
            script {
                echo "Before git log command"
                // Fetch Git information
                def commitAuthor = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()
                def commitEmail = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                def commitDate = sh(script: "git log -1 --pretty=format:'%ad' --date=iso", returnStdout: true).trim()
                def commitMessage = sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
                echo "After git log command"


                // Repository and branch information
                def repoUrl = scm.getUserRemoteConfigs()[0].getUrl()
                def branch = env.GIT_BRANCH
                def payload

                echo "Before payload command"
                // Define the payload for the notification
                try{
                    payload = """
                    {
                        "@type": "MessageCard",
                        "@context": "https://schema.org/extensions",
                        "summary": "Push Event Notification",
                        "themeColor": "FF0000",
                        "title": "Push Event Notification",
                        "sections": [
                            {
                                "activityTitle": "Jenkins Build Notification",
                                "activitySubtitle": "${BUILD_TIMESTAMP}",
                                "activityImage": "https://www.jenkins.io/images/logos/formal/256.png",
                                "facts": [
                                    {
                                        "name": "Author:",
                                        "value": "${commitAuthor} (${commitEmail})"
                                    },
                                    {
                                        "name": "Commit Date:",
                                        "value": "${commitDate}"
                                    },
                                    {
                                        "name": "Commit Message:",
                                        "value": "${commitMessage}"
                                    },
                                    {
                                        "name": "Repository:",
                                        "value": "${repoUrl}"
                                    },
                                    {
                                        "name": "Branch:",
                                        "value": "${branch}"
                                    },
                                    {
                                        "name": "Status:",
                                        "value": "${currentBuild.currentResult}"
                                    },
                                    {
                                        "name": "Job Name:",
                                        "value": "${env.JOB_NAME} #${env.BUILD_NUMBER}"
                                    },
                                    {
                                        "name": "Start Time:",
                                        "value": "${currentBuild.startTimeInMillis}"
                                    },
                                    {
                                        "name": "Duration:",
                                        "value": "${currentBuild.durationString.replace(' and counting', '')}"
                                    },
                                    {
                                        "name": "Remarks:",
                                        "value": "Build triggered by push event"
                                    }
                                ],
                                "text": "Push Event Message"
                            }
                        ],
                        "potentialAction": [
                            {
                                "@type": "OpenUri",
                                "name": "View Commit in Repository",
                                "targets": [
                                    {
                                        "os": "default",
                                        "uri": "${repoUrl}/commit/${env.GIT_COMMIT}"
                                    }
                                ]
                            },
                            {
                                "@type": "OpenUri",
                                "name": "View Build Logs in Jenkins",
                                "targets": [
                                    {
                                        "os": "default",
                                        "uri": "${env.BUILD_URL}"
                                    }
                                ]
                            }
                        ]
                    }"""
                } catch (Exception e) {
                    echo "Error Payload: ${e.message}"
                }
                echo "After payload command"

                try{
                    // Send the notification using an HTTP POST request
                    httpRequest(
                            httpMode: 'POST',
                            acceptType: 'APPLICATION_JSON',
                            httpProxy: 'http://192.168.168.121:8080',
                            contentType: 'APPLICATION_JSON',
                            url: "https://bankbnitbk.webhook.office.com/webhookb2/6a492ca8-c8a1-4ffc-a8de-55ddc178b7e0@56a5465f-c594-43ef-b2e1-2017477901c7/IncomingWebhook/86e6b5f71f8b4f22b43cafee43d28b0a/3b9ac1c5-1935-4edd-bd37-6402ae41dc94",
                            requestBody: payload
                    )
                } catch (Exception e) {
                    echo "Error POST: ${e.message}"
                }


            }



            cleanWs()
        }
        failure {
            script {
                echo "Before git log command"
                // Fetch Git information
                def commitAuthor = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()
                def commitEmail = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                def commitDate = sh(script: "git log -1 --pretty=format:'%ad' --date=iso", returnStdout: true).trim()
                def commitMessage = sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
                echo "After git log command"

                // Repository and branch information
                def repoUrl = scm.getUserRemoteConfigs()[0].getUrl()
                def branch = env.GIT_BRANCH
                def payload

                echo "Before payload command"
                // Define the payload for the notification
                try{
                    payload = """
                    {
                        "@type": "MessageCard",
                        "@context": "https://schema.org/extensions",
                        "summary": "Push Event Notification",
                        "themeColor": "FF0000",
                        "title": "Push Event Notification",
                        "sections": [
                            {
                                "activityTitle": "Jenkins Build Notification",
                                "activitySubtitle": "${BUILD_TIMESTAMP}",
                                "activityImage": "https://resource-dgb.s3.ap-southeast-1.amazonaws.com/fire.png",
                                "facts": [
                                    {
                                        "name": "Author:",
                                        "value": "${commitAuthor} (${commitEmail})"
                                    },
                                    {
                                        "name": "Commit Date:",
                                        "value": "${commitDate}"
                                    },
                                    {
                                        "name": "Commit Message:",
                                        "value": "${commitMessage}"
                                    },
                                    {
                                        "name": "Repository:",
                                        "value": "${repoUrl}"
                                    },
                                    {
                                        "name": "Branch:",
                                        "value": "${branch}"
                                    },
                                    {
                                        "name": "Status:",
                                        "value": "${currentBuild.currentResult}"
                                    },
                                    {
                                        "name": "Job Name:",
                                        "value": "${env.JOB_NAME} #${env.BUILD_NUMBER}"
                                    },
                                    {
                                        "name": "Start Time:",
                                        "value": "${currentBuild.startTimeInMillis}"
                                    },
                                    {
                                        "name": "Duration:",
                                        "value": "${currentBuild.durationString.replace(' and counting', '')}"
                                    },
                                    {
                                        "name": "Remarks:",
                                        "value": "Build triggered by push event"
                                    }
                                ],
                                "text": "Push Event Message"
                            }
                        ],
                        "potentialAction": [
                            {
                                "@type": "OpenUri",
                                "name": "View Commit in Repository",
                                "targets": [
                                    {
                                        "os": "default",
                                        "uri": "${repoUrl}/commit/${env.GIT_COMMIT}"
                                    }
                                ]
                            },
                            {
                                "@type": "OpenUri",
                                "name": "View Build Logs in Jenkins",
                                "targets": [
                                    {
                                        "os": "default",
                                        "uri": "${env.BUILD_URL}"
                                    }
                                ]
                            }
                        ]
                    }"""
                } catch (Exception e) {
                    echo "Error Payload: ${e.message}"
                }
                echo "After payload command"

                try{
                    // Send the notification using an HTTP POST request
                    httpRequest(
                            httpMode: 'POST',
                            acceptType: 'APPLICATION_JSON',
                            httpProxy: 'http://192.168.168.121:8080',
                            contentType: 'APPLICATION_JSON',
                            url: "https://bankbnitbk.webhook.office.com/webhookb2/6a492ca8-c8a1-4ffc-a8de-55ddc178b7e0@56a5465f-c594-43ef-b2e1-2017477901c7/IncomingWebhook/86e6b5f71f8b4f22b43cafee43d28b0a/3b9ac1c5-1935-4edd-bd37-6402ae41dc94",
                            requestBody: payload
                    )
                } catch (Exception e) {
                    echo "Error POST: ${e.message}"
                }
            }
            cleanWs()
        }
        aborted {
            script {
                echo "Aborted"
            }
        }
    }
}
