package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.ProgressCallback;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;


public class EclipseProgressCallback
  implements ProgressCallback
{
  private static final int INTROSPECTION_FACTOR = 2000;
  private static final int GENERATION_FACTOR = 4000;
  private static final int SAVE_FACTOR = 4000;
  private SubMonitor parentProgress;
  private SubMonitor currentChildProgress;
  private int currentTick;

  public EclipseProgressCallback(IProgressMonitor progressMonitor)
  {
    this.parentProgress = SubMonitor.convert(progressMonitor, 
      10000);
  }

  public void checkCancel()
    throws InterruptedException
  {
    if (this.currentChildProgress.isCanceled())
      throw new InterruptedException();
  }

  public void generationStarted(int totalTasks)
  {
    this.currentChildProgress = this.parentProgress.newChild(4000);
    this.currentTick = (totalTasks <= 0 ? 4000 : 4000 / totalTasks);
    if (this.currentTick == 0) {
      this.currentTick = 1;
    }

    this.currentChildProgress.beginTask("Generating Files", 4000);
  }

  public void introspectionStarted(int totalTasks) {
    this.currentChildProgress = this.parentProgress.newChild(2000);
    this.currentTick = (totalTasks <= 0 ? 2000 : 2000 / totalTasks);
    if (this.currentTick == 0) {
      this.currentTick = 1;
    }

    this.currentChildProgress.beginTask("Introspecting Tables", 2000);
  }

  public void saveStarted(int totalTasks) {
    this.currentChildProgress = this.parentProgress.newChild(4000);
    this.currentTick = (totalTasks <= 0 ? 4000 : 4000 / totalTasks);
    if (this.currentTick == 0) {
      this.currentTick = 1;
    }

    this.currentChildProgress.beginTask("Saving Generated Files", 4000);
  }

  public void startTask(String taskName) {
    this.currentChildProgress.subTask(taskName);
    this.currentChildProgress.worked(this.currentTick);
  }

  public void done()
  {
  }
}