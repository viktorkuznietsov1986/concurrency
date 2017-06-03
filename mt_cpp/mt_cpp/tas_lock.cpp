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
		
		bool lockValue;

		while(locked.compare_exchange_strong(lockValue, true)) {

		}

	}

	void tas_lock::unlock() {
		locked.store(false);
	}

}
