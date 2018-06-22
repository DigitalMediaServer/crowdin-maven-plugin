package org.digitalmediaserver.crowdin.tool;

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

	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + getGroupId().hashCode();
		result = 37 * result + getArtifactId().hashCode();
		return result;
	}

	@Override
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

	@Override
	public void addMetadata(ArtifactMetadata arg0) {
		delegator.addMetadata(arg0);
	}

	@Override
	public int compareTo(Artifact o) {
		return delegator.compareTo(o);
	}

	@Override
	public ArtifactHandler getArtifactHandler() {
		return delegator.getArtifactHandler();
	}

	@Override
	public String getArtifactId() {
		return delegator.getArtifactId();
	}

	@Override
	public List<ArtifactVersion> getAvailableVersions() {
		return delegator.getAvailableVersions();
	}

	@Override
	public String getBaseVersion() {
		return delegator.getBaseVersion();
	}

	@Override
	public String getClassifier() {
		return delegator.getClassifier();
	}

	@Override
	public String getDependencyConflictId() {
		return delegator.getDependencyConflictId();
	}

	@Override
	public ArtifactFilter getDependencyFilter() {
		return delegator.getDependencyFilter();
	}

	@Override
	public List<String> getDependencyTrail() {
		return delegator.getDependencyTrail();
	}

	@Override
	public String getDownloadUrl() {
		return delegator.getDownloadUrl();
	}

	@Override
	public File getFile() {
		return delegator.getFile();
	}

	@Override
	public String getGroupId() {
		return delegator.getGroupId();
	}

	@Override
	public String getId() {
		return delegator.getId();
	}

	public ArtifactMetadata getMetadata(Class<?> arg0) {
		return delegator.getMetadataList().iterator().next();
	}

	@Override
	public Collection<ArtifactMetadata> getMetadataList() {
		return delegator.getMetadataList();
	}

	@Override
	public ArtifactRepository getRepository() {
		return delegator.getRepository();
	}

	@Override
	public String getScope() {
		return delegator.getScope();
	}

	@Override
	public ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException {
		return delegator.getSelectedVersion();
	}

	@Override
	public String getType() {
		return delegator.getType();
	}

	@Override
	public String getVersion() {
		return delegator.getVersion();
	}

	@Override
	public VersionRange getVersionRange() {
		return delegator.getVersionRange();
	}

	@Override
	public boolean hasClassifier() {
		return delegator.hasClassifier();
	}

	@Override
	public boolean isOptional() {
		return delegator.isOptional();
	}

	@Override
	public boolean isRelease() {
		return delegator.isRelease();
	}

	@Override
	public boolean isResolved() {
		return delegator.isResolved();
	}

	@Override
	public boolean isSelectedVersionKnown() throws OverConstrainedVersionException {
		return delegator.isSelectedVersionKnown();
	}

	@Override
	public boolean isSnapshot() {
		return delegator.isSnapshot();
	}

	@Override
	public void selectVersion(String arg0) {
		delegator.selectVersion(arg0);
	}

	@Override
	public void setArtifactHandler(ArtifactHandler arg0) {
		delegator.setArtifactHandler(arg0);
	}

	@Override
	public void setArtifactId(String arg0) {
		delegator.setArtifactId(arg0);
	}

	@Override
	public void setAvailableVersions(List<ArtifactVersion> arg0) {
		delegator.setAvailableVersions(arg0);
	}

	@Override
	public void setBaseVersion(String arg0) {
		delegator.setBaseVersion(arg0);
	}

	@Override
	public void setDependencyFilter(ArtifactFilter arg0) {
		delegator.setDependencyFilter(arg0);
	}

	@Override
	public void setDependencyTrail(List<String> arg0) {
		delegator.setDependencyTrail(arg0);
	}

	@Override
	public void setDownloadUrl(String arg0) {
		delegator.setDownloadUrl(arg0);
	}

	@Override
	public void setFile(File arg0) {
		delegator.setFile(arg0);
	}

	@Override
	public void setGroupId(String arg0) {
		delegator.setGroupId(arg0);
	}

	@Override
	public void setOptional(boolean arg0) {
		delegator.setOptional(arg0);
	}

	@Override
	public void setRelease(boolean arg0) {
		delegator.setRelease(arg0);
	}

	@Override
	public void setRepository(ArtifactRepository arg0) {
		delegator.setRepository(arg0);
	}

	@Override
	public void setResolved(boolean arg0) {
		delegator.setResolved(arg0);
	}

	@Override
	public void setResolvedVersion(String arg0) {
		delegator.setResolvedVersion(arg0);
	}

	@Override
	public void setScope(String arg0) {
		delegator.setScope(arg0);
	}

	@Override
	public void setVersion(String arg0) {
		delegator.setVersion(arg0);
	}

	@Override
	public void setVersionRange(VersionRange arg0) {
		delegator.setVersionRange(arg0);
	}

	@Override
	public void updateVersion(String arg0, ArtifactRepository arg1) {
		delegator.updateVersion(arg0, arg1);
	}


}
