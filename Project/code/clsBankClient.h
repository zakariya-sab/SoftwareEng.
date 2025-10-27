#pragma once
#include <iostream>
#include <string>
#include "clsPerson.h"
#include "clsString.h"
#include <vector>
#include <fstream>
//this file contain all client 
//const string Client_file = "Client.txt";

class clsBankClient : public clsPerson
{
private:

	enum enMode { EmptyMode = 0, UpdateMode = 1 };
	enMode _Mode;
	string _AccountNumber;
	string _PinCode;
	float _AccountBalance;

	static clsBankClient _ConvertLineToClientObject(string Line,string Seperator = "#//#") {

		vector<string> Client = clsString::Split(Line, Seperator);
		return clsBankClient(enMode::UpdateMode,  Client[0],Client[1],Client[2],Client[3],Client[4],Client[5], stod(Client[6]));
	}

	static clsBankClient _GetEmptyClientObject() {

		return clsBankClient(enMode::EmptyMode,  "","","","","","",0);
	}

	static  vector<clsBankClient> _LoadClientDataFormFile() {

		vector<clsBankClient> vClients;
		fstream MyFile;

		MyFile.open("Clients.txt", ios::in);

		if (MyFile.is_open())
		{
			string Line;
			while (getline(MyFile, Line)) {

				clsBankClient Client = _ConvertLineToClientObject(Line);
				vClients.push_back(Client);
				
			}

			MyFile.close();
			return vClients;

		}
		return vClients;


	}

	static string _ConvertClientObjectToLine(clsBankClient Client,string Seperator = "#//#") {
		string ClientRecords;
		ClientRecords = Client.GetFirstName() + Seperator;
		ClientRecords += Client.GetLastName() + Seperator;
		ClientRecords += Client.GetEmail() + Seperator;
		ClientRecords += Client.GetPhone() + Seperator;
		ClientRecords += Client.AccountNumber() + Seperator;
		ClientRecords += Client.GetPinCode() + Seperator;
		ClientRecords += to_string(Client._AccountBalance) + Seperator;

		return ClientRecords;

   }

	static void _SaveClientDataToFiles(vector<clsBankClient> vClients) {

		fstream MyFile;

		MyFile.open("Clients.txt", ios::out);

		if (MyFile.is_open())
		{
			string Line;
			for (const clsBankClient& Client : vClients)
			{
				Line = _ConvertClientObjectToLine(Client);
				MyFile << Line << endl;
			}

			MyFile.close();

		}


	}

	void _Update() {

		vector<clsBankClient> vClients;
		vClients = _LoadClientDataFormFile();

		for (clsBankClient &C : vClients) {

			if (C.AccountNumber() == AccountNumber())
			{
				C = *this;
				break;
			}

		}

		_SaveClientDataToFiles(vClients);

	}

	public:

		clsBankClient(enMode Mode, string FirstName, string LastName, string Email, string Phone, string AccountNumber, string PinCode, float AccountBalance)
			:clsPerson(FirstName, LastName, Email, Phone)
		{
			_Mode = Mode;
			_AccountNumber = AccountNumber;
			_PinCode= PinCode;
		   _AccountBalance= AccountBalance;
		};

	    bool IsEmpty() {
			return (_Mode == enMode::EmptyMode) ;
		}

		string AccountNumber() {
			return _AccountNumber;
		}

		void SetPinCode(string PinCode) {
			_PinCode = PinCode;
		}

		string GetPinCode() {
			return _PinCode;
		}

	

		void SetAccountBalance(float AccountBalance) {
			_AccountBalance = AccountBalance;
		}

		float GetAccountBalance() {
			return _AccountBalance;
		}

		

		static clsBankClient Find(string AccountNumber) {

			fstream MyFile;

			MyFile.open("Clients.txt",ios::in);

			if (MyFile.is_open())
			{
				string Line;
				while (getline(MyFile, Line)) {

					clsBankClient Client = _ConvertLineToClientObject(Line);

					if (Client.AccountNumber() == AccountNumber)
					{
						MyFile.close();
						return Client;

					}
				}

				MyFile.close();

			}
		
			return _GetEmptyClientObject();

		}

		static clsBankClient Find(string AccountNumber,string PinCode) {

			fstream MyFile;

			MyFile.open("Clients.txt",ios::in);

			if (MyFile.is_open())
			{
				string Line;
				while (getline(MyFile, Line)) {

					clsBankClient Client = _ConvertLineToClientObject(Line);

					if (Client.AccountNumber() == AccountNumber && Client.GetPinCode() == PinCode)
					{
						MyFile.close();
						return Client;

					}
				}

				MyFile.close();

			}
		
			return _GetEmptyClientObject();

		}

	     static bool IsClientExist(string AccountNumber) {

			 clsBankClient Client = Find(AccountNumber);
			return (!Client.IsEmpty());
		}

		 void Print()
		 {
			 cout << "\nClient Card:";
			 cout << "\n___________________";
			 cout << "\nFirstName   : " << GetFirstName();
			 cout << "\nLastName    : " << GetLastName();
			 cout << "\nFull Name   : " << FullName();
			 cout << "\nEmail       : " << GetEmail();
			 cout << "\nPhone       : " << GetPhone();
			 cout << "\nAcc. Number : " << _AccountNumber;
			 cout << "\nPassword    : " << _PinCode;
			 cout << "\nBalance     : " << _AccountBalance;
			 cout << "\n___________________\n";

		 }

		 enum enSaveResults {svFaildEmptyObject = 0 , svSucceeded =1 };

		 enSaveResults Save() {

			 switch (_Mode) {
			 case enMode::EmptyMode:
			 {
				 return enSaveResults::svFaildEmptyObject;
				 break;
			 }
			 case enMode::UpdateMode:
			 {
				 _Update();

				 return enSaveResults::svSucceeded;
				 break;
			 }

			 }
		 }


};