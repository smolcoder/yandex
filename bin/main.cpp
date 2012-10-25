#pragma comment(linker, "/stack:16777216")

#include <iostream>
#include <algorithm>
#include <map>
#include <string>
#include <vector>
//#include <windows.h>

using namespace std;

//struct suffixarray
//{
//    struct state 
//    {
//	    int length, link, endpos;
//	    map<char,int> next;
//    };    
//};



struct state 
{
	int len, link, endpos;
	map<char,int> next;
};

const int maxlen = 100010;
state st[maxlen*3 + 1];
int sz, last;

void sa_init() {
	sz = last = 0;
	st[0].len = 0;
	st[0].link = -1;
	++sz;	
}

void sa_extend (char c, int pos) {
    int cur = sz++;
    st[cur].len = st[last].len + 1;
    st[cur].endpos = pos;
    int p;
    for (p=last; p!=-1 && !st[p].next.count(c); p=st[p].link)
        st[p].next[c] = cur;
    if (p == -1)
        st[cur].link = 0;
    else {
        int q = st[p].next[c];
        if (st[p].len + 1 == st[q].len)
            st[cur].link = q;
        else {
			int clone = sz++;
			st[clone].len = st[p].len + 1;
			st[clone].next = st[q].next;
			st[clone].link = st[q].link;
            st[clone].endpos = pos;
			for (; p!=-1 && st[p].next[c]==q; p=st[p].link)
				st[p].next[c] = clone;
			st[q].link = st[cur].link = clone;
		}
	}   
	last = cur;
}

bool visit[maxlen*3 + 1];
int masks[maxlen*3 + 1];

void dfs(char c, int fr)
{
    if (visit[fr])
        return;
    visit[fr] = true;
    map<char, int>::iterator iter = st[fr].next.begin();
    while (iter != st[fr].next.end())
    {        
        char nc = (*iter).first;
        int to = (*iter).second;
        //cout << fr + 1 << "->" << to + 1 << " " << nc << "\n";
        if (!visit[to])
            dfs(nc, to);
        if ('a' <= nc && nc <= 'z')
            masks[fr] |= 1 << (nc - 'a');
        else
            masks[fr] |= masks[to];
        iter++;
    }
}

string lcs(vector<string> & strings)
{
    char delimiters[10] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
    string s = "";
    for (int i = 0; i < strings.size(); ++i)
        s += (strings[i] + delimiters[i]);
    //cout << s << endl;
    sa_init();
    for (int i = 0; i < s.length(); ++i)
        sa_extend(s[i], i);
    fill(visit, visit + sz + 1, false);
    fill(masks, masks + sz + 1, 0);
    dfs(' ', 0);
    int ans = 0;
    int bstate = -1;
    for (int i = 0; i < sz; ++i)
    {
        if ((masks[i] == ((1 << strings.size()) - 1)) && (st[i].len > ans))
        {
            ans = st[i].len;
            bstate = i;
        }
    }
    if (bstate == -1)
        return "";
        
    //cout << ans << " " << st[bstate].endpos + 1;
    //return s.substr(st[bstate].endpos - ans + 1, ans);    
    return "";
}

int main() 
{
    freopen("in.txt", "r", stdin);
    int k;
    cin >> k;
    vector<string> s(k);
    for (int i = 0; i < k; ++i)
        cin >> s[i];
    cout << lcs(s);
    return 0;
}