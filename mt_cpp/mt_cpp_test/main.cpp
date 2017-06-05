
//#include "tas_lock.h"
#include <thread>
#include <iostream>

int main() {

	std::thread t([] {std::cout << "test" << std::endl; });
	t.join();

	std::cin.get();

	return 0;
}