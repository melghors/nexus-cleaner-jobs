import org.sonatype.nexus.repository.storage.StorageFacet;
import org.sonatype.nexus.common.app.GlobalComponentLookupHelper
import org.sonatype.nexus.repository.maintenance.MaintenanceService
import org.sonatype.nexus.repository.storage.ComponentMaintenance
import org.sonatype.nexus.repository.storage.Query;
import org.sonatype.nexus.script.plugin.RepositoryApi
import org.sonatype.nexus.script.plugin.internal.provisioning.RepositoryApiImpl
import com.google.common.collect.ImmutableList
import org.joda.time.DateTime;
import org.slf4j.Logger

def retentionCount = 10;
def repositoryName = 'my_nexus_repo';
def blacklist = ["component_group/component"].toArray();

log.info(":::Cleanup script started!");
MaintenanceService service = container.lookup("org.sonatype.nexus.repository.maintenance.MaintenanceService");
def repo = repository.repositoryManager.get(repositoryName);
def tx = repo.facet(StorageFacet.class).txSupplier().get();
def components = null;
try {
    tx.begin();
    components = tx.browseComponents(tx.findBucket(repo));
}catch(Exception e){
    log.info("Error: "+e);
}finally{
    if(tx != null)
        tx.close();
}

if (components != null) {
    int deletedComponentCount = 0;
    int compCount = 0;
    def listOfComponents = ImmutableList.copyOf(components);
    def previousComp = listOfComponents.head().group() + listOfComponents.head().name();

    listOfComponents.reverseEach{comp ->
        log.info("Processing Component - group: ${comp.group()}, ${comp.name()}, version: ${comp.version()}");
        if (blacklist.contains(comp.group() + "/" +comp.name())) {
            log.info("Previous: ${previousComp}");
            if (previousComp.equals(comp.group() + comp.name())) {
                compCount++;
                log.info("CompCount: ${compCount}, RetCount: ${retentionCount}");
                if (compCount > retentionCount) {
                    log.info("Deleting ${comp.group()}, ${comp.name()}, version: ${comp.version()}");
                    service.deleteComponent(repo, comp);
                    log.info("Component deleted");
                    deletedComponentCount++;
                }
            } else {
                compCount = 1;
                previousComp = comp.group() + comp.name();
            }
        } else {
            log.info("Component skipped: ${comp.group()} ${comp.name()}");
        }
    }

    log.info("Deleted Component count: ${deletedComponentCount}");
    log.info(":::Cleanup script finished!");
}
