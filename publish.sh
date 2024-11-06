set -o allexport

echo "Publishing..."

./gradlew :rapidroid-actor:publishToMavenLocal
./gradlew :rapidroid-core:publishToMavenLocal
./gradlew :rapidroid-future:publishToMavenLocal
./gradlew :rapidroid-workflow:publishToMavenLocal
./gradlew :rapidroid-api:publishToMavenLocal
