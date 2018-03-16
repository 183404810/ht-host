package ht.msc.biny;

public interface Factory<T>{
	public void Init();
	public void destory();	
	public T createInstance();
	public void release(T o);
}
