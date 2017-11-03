#include<iostream>  
#include<stack>
#include<stdlib.h>

using namespace std;




int main() {
	stack<char> ops;
	stack<double> vals;
	bool error = false;
	string s;
	char c=NULL;
	cout << "\n��������ʽ��֧��+ - * / # ( ) ���ԵȺŽ���\n���磺 1+2*3= \n ֧�ֶ������,����b�˳�\n" << endl;     //��ʾ����
	while (true) {
		
		while (cin >> c) {                        //����
			if (!((c >= '0'&&c <= '9') || c == '+' || c == '-' || c == '*' || c == '/' || c == '#' || c == '(' || c == ')' || c == '=' ||c == 'b' || c == '.')) {    //�ж�����Ϸ���
				cout << "\nerror:�зǷ��ַ�����\n" << endl;
				error = true;
				break;
			}
			if (c == 'b')                                     //��Ӧ�˳�ָ��
				return 0;
			if (c == '=') {                                   //�ж���������
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				break;
			}
			else if (c == '(') {
				ops.push(c);
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);

			}
			else if (c == '+' || c == '-') {
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);
				if (ops.size() != 0 && (ops.top() == '*' || ops.top() == '/' || ops.top() == '#')) {
					if (vals.size() == 0) {               //�ж�����Ϸ���
						cout << "#���޲�����" << endl;
						error = true;
						break;
					}
					double temp = vals.top();
					vals.pop();
					if (ops.top() == '*') {
						ops.pop();
						double temp1 = temp + vals.top();
						vals.pop();
						vals.push(temp1);
					}
					else if (ops.top() == '/') {
						ops.pop();
						double temp1 = vals.top() - temp;
						vals.pop();
						vals.push(temp1);
					}
					else if (ops.top() == '#') {
						ops.pop();
						double temp1 = -temp;
						vals.push(temp1);
					}
				}
				if (vals.size() == 0) {                                     //�ж�����Ϸ���
					cout << "���������������ƥ��(����������)" << endl;
					error = true;
					break;
				}
				ops.push(c);
			}
			else if (c == '*' || c == '/') {
				ops.push(c);
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);
			}
			else if (c == '#') {
				ops.push(c);
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);
			}
			else if (c == ')') {                                          //�����ڼ���
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);
				if (ops.size() == 0) {                                      //�ж�����Ϸ���
					cout << "ȱʧ(" << endl;
					error = true;
					break;
				}
				while (ops.top() != '(') {
					char temp = ops.top();
					ops.pop();
					if (vals.size() == 0) {                                //�ж�����Ϸ���
						cout << "()���޲�����" << endl;
						error = true;
						break;
					}
					double v = vals.top();
					vals.pop();
					if (ops.size()!=0&&ops.top() != '#'&&vals.size() == 0) {       //�ж�����Ϸ���
						cout << "( )�ڲ��������������ƥ��" << endl;
						error = true;
						break;
					}
					

					if (temp == '+') {
						v = v + vals.top();
						vals.pop();
					}
					else if (temp == '-') {
						v = vals.top() - v;
						vals.pop();
					}
					else if (temp == '*') {
						v = v*vals.top();
						vals.pop();
					}
					else if (temp == '/') {
						v = vals.top() / v;
						vals.pop();
					}
					else if (temp == '#') {
						v = -v;
					}
					if (ops.size() == 0 && (vals.size() == 0||vals.top() != '(')) {        //�ж�����Ϸ���
						cout << "ȱʧ(" << endl;
						error = true;
						break;
					}
					vals.push(v);
				}
				if (ops.size() == 0) {
					error = true;
					break;
				}
				ops.pop();
			}
			else
				s = s + c;
		}

		while (ops.size() != 0) {                                    //�ж�����Ϸ���
			if (vals.size() == 0) {
				cout << "���������������ƥ��(����������)" << endl;
				error = true;
				break;
			}
			double temp = vals.top();
			double v;
			vals.pop();
			if (ops.top() != '#'&&vals.size() == 0) {               //�ж�����Ϸ���
				cout << "���������������ƥ��(����������)" << endl;
				error = true;
				break;
			}
			switch (ops.top()) {                                    //��ͬ���ȼ���ʽ�ļ���
			case '+': v = temp + vals.top(); vals.pop(); break;
			case '-': v = vals.top() - temp; vals.pop(); break;
			case '*': v = vals.top() * temp; vals.pop(); break;
			case '/': v = vals.top() / temp; vals.pop(); break;
			case '#': v = -temp; break;
			case '(':                                                //�ж�����Ϸ���
			case ')':cout << "���Ų�ƥ��" << endl; v = 0; error = true; break;
			}
			ops.pop();
			vals.push(v);
		}
		if (error)                                                  //��Ӧ���д�����ʾ��������
			cout << "����������" << endl;
		else {
			cout << vals.top() << endl;			
		}
		string().swap(s);                                           //�ÿձ�����ջ
		while (!vals.empty())
			vals.pop();
		while (!ops.empty())
			ops.pop();
		error = false;
		char sbuf[1024];
		fgets(sbuf, 1024, stdin);
	}
	return 0;
}


