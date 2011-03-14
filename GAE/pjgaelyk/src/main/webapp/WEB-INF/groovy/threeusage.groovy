import com.freesundance.http.ThreeUsage
import com.freesundance.http.gae.GAEConnectionManager

log.info "Getting latest usage"

request.setAttribute 'threeusage', ThreeUsage.doIt("07413770734", "skyboy125", new GAEConnectionManager())

log.info "Forwarding to the template"

forward '/threeusage.gtpl'