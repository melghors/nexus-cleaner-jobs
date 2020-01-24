import org.sonatype.nexus.repository.maintenance.MaintenanceService
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.StorageFacet
import org.sonatype.nexus.repository.Repository
import com.google.common.collect.ImmutableList
import org.joda.time.DateTime
import java.util.regex.Pattern
import org.sonatype.nexus.script.ScriptManager
import org.sonatype.nexus.common.script.ScriptService
import org.sonatype.nexus.repository.storage.Asset

log.info(":::Cleanup script started!")

def repositoryName = "docker_test"
def retentionDays = 7
def retentionCount = 10
def pattern = ~/^([a-zA-Z]+)_/
def patternSemver = ~/^\d+\.\d+\.\d+-([a-zA-Z0-9-]+)/
def blacklist = ["null/another", "null/composer"].toArray()

log.info("*** Proceeding with repository: $repositoryName")

MaintenanceService service = container.lookup("org.sonatype.nexus.repository.maintenance.MaintenanceService") as MaintenanceService
def repo = repository.repositoryManager.get(repositoryName)
def tx = repo.facet(StorageFacet.class).txSupplier().get()
def components = null
try {
    tx.begin()
    components = ImmutableList.copyOf(tx.findComponents(Query.builder().suffix(' ORDER BY name ASC, last_updated ASC').build(), [repo]))

    if(components != null && components.size() > 0) {
      def retentionDate = DateTime.now().minusDays(retentionDays).dayOfMonth().roundFloorCopy()
      log.info(retentionDate)
      int deletedComponentCount = 0
      int compCount = 0
      def listOfComponents = components
      def tagCount = [:]

      def previousComp = listOfComponents.head().group() + listOfComponents.head().name()

      listOfComponents.reverseEach{comp ->
          if(blacklist.contains(comp.group()+"/"+comp.name())){

            if(previousComp != (comp.group() + comp.name())) {
                compCount = 0
                tagCount = [:]
                previousComp = comp.group() + comp.name()
                log.info("Tag: ${comp.group()}/${comp.name()}")
            }

            if(comp.version() == 'latest'){
                log.info("    SKIPPING ${comp.group()}/${comp.name()}, version: ${comp.version()} (tag == latest).")
                return
            }

            def prefix = null
            def matcher = comp.version() =~ pattern
            if(matcher) {
                prefix = matcher.group(1)
            } else{
                matcher = comp.version() =~ patternSemver
                if(matcher) {
                    prefix = matcher.group(1)
                }
            }
            def actualCount
            if(prefix != null) {
                if(tagCount[prefix] == null) {
                    tagCount[prefix] = 0
                }
                tagCount[prefix]++
                actualCount = tagCount[prefix]
                log.info("version: ${comp.version()}, prefix: ${prefix}")
            } else {
                compCount++
                actualCount = compCount
                log.info("version: ${comp.version()}")
            }
            log.info("CompCount: ${actualCount}, RetCount: ${retentionCount}")
            if(actualCount > retentionCount) {
              tx.browseAssets(comp).each { Asset asset ->
                if (asset.lastDownloaded() == null) {
                    log.info("    DELETING ${comp.group()}/${comp.name()}, version: ${comp.version()}");
                    tx.deleteComponent(comp)
                    deletedComponentCount++;
                } else if (asset.lastDownloaded().isBefore(retentionDate)) {
                    log.info("    DELETING ${comp.group()}/${comp.name()}, version: ${comp.version()}");
                    tx.deleteComponent(comp)
                    deletedComponentCount++;
                }
              }
            }
          }
      }
      tx.commit();
      log.info("Deleted Component count: ${deletedComponentCount}")
    }
}catch(Exception e){
    log.info("Error: "+e)
}finally{
    if(tx!=null)
        tx.close()
}