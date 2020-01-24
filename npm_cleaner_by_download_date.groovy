import org.apache.commons.io.FileUtils
import org.sonatype.nexus.repository.storage.Asset
import org.sonatype.nexus.repository.storage.Component;
import org.sonatype.nexus.repository.storage.Query
import org.sonatype.nexus.repository.storage.Query.Builder;
import org.sonatype.nexus.repository.storage.StorageFacet
import org.joda.time.DateTime;

def repositoryName = '';
def assetGroup = '';
int retentionDays = 7;
def blacklist = ["one", "two", "three"].toArray();

def repo = repository.repositoryManager.get(repositoryName)
StorageFacet storageFacet = repo.facet(StorageFacet)

def tx = storageFacet.txSupplier().get()

try {

    tx.begin()

    Builder qb = Query.builder()
    qb.where('group = ').param(assetGroup)

    Iterable<Component> comps = tx.
            findComponents(qb.build(), [repo])

    def retentionDate = new DateTime().minusDays(retentionDays);

    int deletedComponentCount = 0;

    log.info(":::Cleanup script started!");

    comps.each { Component comp ->
        if (blacklist.contains(comp.name())) {
            if (comp.version().contains("beta") | comp.version().contains("next")) {
                tx.browseAssets(comp).each { Asset asset ->
                    if (asset.lastDownloaded() == null) {
                        log.info("Deleting ${comp.group()}/${comp.name()}, version: ${comp.version()}");
                        tx.deleteComponent(comp);
                        deletedComponentCount++;
                    } else if (asset.lastDownloaded().isBefore(retentionDate)) {
                        log.info("Deleting ${comp.group()}/${comp.name()}, version: ${comp.version()}");
                        tx.deleteComponent(comp);
                        deletedComponentCount++;
                    }
                }
            }
        }
    }
    tx.commit();
    log.info("Deleted Components count: ${deletedComponentCount}");
    log.info(":::Cleanup script finished!");

}
finally {
    tx.close()
}
