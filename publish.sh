set -o allexport

echo "Type Archiva username:"
read -r MAVEN_PUBLISH_USERNAME
echo "Type Archiva password:"
read -s -r MAVEN_PUBLISH_PASSWORD

echo "Publishing..."

./gradlew :rapidroid-actor:publish
./gradlew :rapidroid-core:publish
./gradlew :rapidroid-future-java:publish
./gradlew :rapidroid-future-java7:publish
./gradlew :rapidroid-future-kotlin:publish
./gradlew :rapidroid-workflow:publish
./gradlew :rapidroid-api-java:publish
./gradlew :rapidroid-api-java7:publish
./gradlew :rapidroid-api-kotlin:publish

set +o allexport

read -s -r TEMP
