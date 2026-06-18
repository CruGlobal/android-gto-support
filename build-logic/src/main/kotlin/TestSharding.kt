import org.gradle.api.Project

internal fun Project.configureTestSharding() {
    val shard = findProperty("testShard")?.toString()?.toIntOrNull()
    val totalShards = findProperty("testTotalShards")?.toString()?.toIntOrNull()
    if (shard != null && totalShards != null) {
        if (Math.floorMod(path.hashCode(), totalShards) != Math.floorMod(shard, totalShards)) {
            androidComponents.beforeVariants { it.enableUnitTest = false }
        }
    }
}
