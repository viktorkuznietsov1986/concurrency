#include "tas_lock.h"

namespace locks {

	tas_lock::tas_lock()
	{
		locked = false;
	}


	tas_lock::~tas_lock()
	{
	}

	void tas_lock::lock() {
		
		bool oldValue = locked.load();

		while(locked.compare_exchange_strong(oldValue, true)) {

		}

	}

	void tas_lock::unlock() {
		locked.store(false);
	}

}
