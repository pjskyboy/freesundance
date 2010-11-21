import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.extensions.LastModifiedBy;


public class Utilities {
	
		
		public static String entryToString(DocumentListEntry entry) {
			String resourceId = entry.getResourceId();
			String docType = entry.getType();
			
			StringBuffer sb = new StringBuffer();
			sb.append("\n").append("'" + entry.getTitle().getPlainText() + "' (" + docType + ")");
			sb.append("\n").append("  link to Google Docs: " + entry.getDocumentLink().getHref());
			sb.append("\n").append("  resource id: " + resourceId);
			sb.append("\n").append("  doc id: " + entry.getDocId());
			
			// print the parent folder the document is in
			if (!entry.getParentLinks().isEmpty()) {
				sb.append("\n").append("  Parent folders: ");
				for (Link link : entry.getParentLinks()) {
					sb.append("\n").append("    --" + link.getTitle() + " - " + link.getHref());
				}
			}
			
			// print the timestamp the document was last viewed
			DateTime lastViewed = entry.getLastViewed();
			if (lastViewed != null) {
				sb.append("\n").append("  last viewed: " + lastViewed.toUiString());
			}
			
			// print who made the last modification
			LastModifiedBy lastModifiedBy = entry.getLastModifiedBy();
			if (lastModifiedBy != null) {
				sb.append("\n").append("  updated by: " +
						lastModifiedBy.getName() + " - " + lastModifiedBy.getEmail());
			}
			
			// Files such as PDFs take up quota
			if (entry.getQuotaBytesUsed() > 0) {
				sb.append("\n").append("Quota used: " + entry.getQuotaBytesUsed() + " bytes");
			}
			
			// print other useful metadata
			sb.append("\n").append("  last updated: " + entry.getUpdated().toUiString());
			sb.append("\n").append("  viewed by user? " + entry.isViewed());
			sb.append("\n").append("  writersCanInvite? " + entry.isWritersCanInvite().toString());
			sb.append("\n").append("  hidden? " + entry.isHidden());
			sb.append("\n").append("  starred? " + entry.isStarred());
			sb.append("\n").append("  trashed? " + entry.isTrashed());
			sb.append("\n");
			
			return sb.toString();
		}
	
}
