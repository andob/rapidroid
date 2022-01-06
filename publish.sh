set -o allexport

echo "Type Archiva username:"
read -r MAVEN_PUBLISH_USERNAME
echo "Type Archiva password:"
read -s -r MAVEN_PUBLISH_PASSWORD

echo "Publishing..."

./gradlew :rapidroid-actor:publish
./gradlew :rapidroid-core:publish
./gradlew :rapidroid-future:publish
./gradlew :rapidroid-workflow:publish
./gradlew :rapidroid-api:publish

set +o allexport

read -s -r TEMP
