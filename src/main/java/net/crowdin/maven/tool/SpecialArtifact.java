package net.crowdin.maven.tool;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

public class SpecialArtifact implements Artifact {

	private Artifact delegator;

	public SpecialArtifact(Artifact delegator) {
		super();
		this.delegator = delegator;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + getGroupId().hashCode();
		result = 37 * result + getArtifactId().hashCode();
		return result;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Artifact)) {
			return false;
		}
		Artifact a = (Artifact) o;
		if (!a.getGroupId().equals(getGroupId())) {
			return false;
		} else if (!a.getArtifactId().equals(getArtifactId())) {
			return false;
		}
		// We don't consider the version range in the comparison, just the
		// resolved version
		return true;
	}

	@Override
	public String toString() {
		return getGroupId() + ":" + getArtifactId();
	}

	public void addMetadata(ArtifactMetadata arg0) {
		delegator.addMetadata(arg0);
	}

	public int compareTo(Artifact o) {
		return delegator.compareTo(o);
	}

	public ArtifactHandler getArtifactHandler() {
		return delegator.getArtifactHandler();
	}

	public String getArtifactId() {
		return delegator.getArtifactId();
	}

	public List<ArtifactVersion> getAvailableVersions() {
		return delegator.getAvailableVersions();
	}

	public String getBaseVersion() {
		return delegator.getBaseVersion();
	}

	public String getClassifier() {
		return delegator.getClassifier();
	}

	public String getDependencyConflictId() {
		return delegator.getDependencyConflictId();
	}

	public ArtifactFilter getDependencyFilter() {
		return delegator.getDependencyFilter();
	}

	public List<String> getDependencyTrail() {
		return delegator.getDependencyTrail();
	}

	public String getDownloadUrl() {
		return delegator.getDownloadUrl();
	}

	public File getFile() {
		return delegator.getFile();
	}

	public String getGroupId() {
		return delegator.getGroupId();
	}

	public String getId() {
		return delegator.getId();
	}

	public ArtifactMetadata getMetadata(Class<?> arg0) {
		return delegator.getMetadata(arg0);
	}

	public Collection<ArtifactMetadata> getMetadataList() {
		return delegator.getMetadataList();
	}

	public ArtifactRepository getRepository() {
		return delegator.getRepository();
	}

	public String getScope() {
		return delegator.getScope();
	}

	public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
		return delegator.getSelectedVersion();
	}

	public String getType() {
		return delegator.getType();
	}

	public String getVersion() {
		return delegator.getVersion();
	}

	public VersionRange getVersionRange() {
		return delegator.getVersionRange();
	}

	public boolean hasClassifier() {
		return delegator.hasClassifier();
	}

	public boolean isOptional() {
		return delegator.isOptional();
	}

	public boolean isRelease() {
		return delegator.isRelease();
	}

	public boolean isResolved() {
		return delegator.isResolved();
	}

	public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
		return delegator.isSelectedVersionKnown();
	}

	public boolean isSnapshot() {
		return delegator.isSnapshot();
	}

	public void selectVersion(String arg0) {
		delegator.selectVersion(arg0);
	}

	public void setArtifactHandler(ArtifactHandler arg0) {
		delegator.setArtifactHandler(arg0);
	}

	public void setArtifactId(String arg0) {
		delegator.setArtifactId(arg0);
	}

	public void setAvailableVersions(List<ArtifactVersion> arg0) {
		delegator.setAvailableVersions(arg0);
	}

	public void setBaseVersion(String arg0) {
		delegator.setBaseVersion(arg0);
	}

	public void setDependencyFilter(ArtifactFilter arg0) {
		delegator.setDependencyFilter(arg0);
	}

	public void setDependencyTrail(List<String> arg0) {
		delegator.setDependencyTrail(arg0);
	}

	public void setDownloadUrl(String arg0) {
		delegator.setDownloadUrl(arg0);
	}

	public void setFile(File arg0) {
		delegator.setFile(arg0);
	}

	public void setGroupId(String arg0) {
		delegator.setGroupId(arg0);
	}

	public void setOptional(boolean arg0) {
		delegator.setOptional(arg0);
	}

	public void setRelease(boolean arg0) {
		delegator.setRelease(arg0);
	}

	public void setRepository(ArtifactRepository arg0) {
		delegator.setRepository(arg0);
	}

	public void setResolved(boolean arg0) {
		delegator.setResolved(arg0);
	}

	public void setResolvedVersion(String arg0) {
		delegator.setResolvedVersion(arg0);
	}

	public void setScope(String arg0) {
		delegator.setScope(arg0);
	}

	public void setVersion(String arg0) {
		delegator.setVersion(arg0);
	}

	public void setVersionRange(VersionRange arg0) {
		delegator.setVersionRange(arg0);
	}

	public void updateVersion(String arg0, ArtifactRepository arg1) {
		delegator.updateVersion(arg0, arg1);
	}

	
}
