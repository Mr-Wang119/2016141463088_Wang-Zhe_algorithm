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
	cout << "\n请输入表达式，支持+ - * / # ( ) ，以等号结束\n例如： 1+2*3= \n 支持多次输入,输入b退出\n" << endl;     //提示输入
	while (true) {
		
		while (cin >> c) {                        //输入
			if (!((c >= '0'&&c <= '9') || c == '+' || c == '-' || c == '*' || c == '/' || c == '#' || c == '(' || c == ')' || c == '=' ||c == 'b' || c == '.')) {    //判断输入合法性
				cout << "\nerror:有非法字符请检查\n" << endl;
				error = true;
				break;
			}
			if (c == 'b')                                     //响应退出指令
				return 0;
			if (c == '=') {                                   //判断输入类型
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
					if (vals.size() == 0) {               //判断输入合法性
						cout << "#后无操作数" << endl;
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
				if (vals.size() == 0) {                                     //判断输入合法性
					cout << "操作数与操作符不匹配(操作数过少)" << endl;
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
			else if (c == ')') {                                          //括号内计算
				if (s.compare("") != 0)
					vals.push(atof(s.c_str()));
				string().swap(s);
				if (ops.size() == 0) {                                      //判断输入合法性
					cout << "缺失(" << endl;
					error = true;
					break;
				}
				while (ops.top() != '(') {
					char temp = ops.top();
					ops.pop();
					if (vals.size() == 0) {                                //判断输入合法性
						cout << "()内无操作数" << endl;
						error = true;
						break;
					}
					double v = vals.top();
					vals.pop();
					if (ops.size()!=0&&ops.top() != '#'&&vals.size() == 0) {       //判断输入合法性
						cout << "( )内操作数与操作符不匹配" << endl;
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
					if (ops.size() == 0 && (vals.size() == 0||vals.top() != '(')) {        //判断输入合法性
						cout << "缺失(" << endl;
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

		while (ops.size() != 0) {                                    //判断输入合法性
			if (vals.size() == 0) {
				cout << "操作数与操作符不匹配(操作数过多)" << endl;
				error = true;
				break;
			}
			double temp = vals.top();
			double v;
			vals.pop();
			if (ops.top() != '#'&&vals.size() == 0) {               //判断输入合法性
				cout << "操作数与操作符不匹配(操作数过少)" << endl;
				error = true;
				break;
			}
			switch (ops.top()) {                                    //相同优先级算式的计算
			case '+': v = temp + vals.top(); vals.pop(); break;
			case '-': v = vals.top() - temp; vals.pop(); break;
			case '*': v = vals.top() * temp; vals.pop(); break;
			case '/': v = vals.top() / temp; vals.pop(); break;
			case '#': v = -temp; break;
			case '(':                                                //判断输入合法性
			case ')':cout << "括号不匹配" << endl; v = 0; error = true; break;
			}
			ops.pop();
			vals.push(v);
		}
		if (error)                                                  //响应所有错误，提示重新输入
			cout << "请重新输入" << endl;
		else {
			cout << vals.top() << endl;			
		}
		string().swap(s);                                           //置空变量和栈
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


