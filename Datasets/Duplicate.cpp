#include <bits/stdc++.h>
using namespace std;

#define LL long long
#define ULL unsigned long long
#define INF LLONG_MAX
#define LD long double
#define MAX(a,b) ((a)>(b)?(a):(b))
#define MIN(a,b) ((a)<(b)?(a):(b))
#define MOD 1000000007LL
#define ABS(x) ((x)<0?-(x):(x))
#define line() printf("\n");
#define spc() printf(" ");
#define f(i, a, b) for(i = a; i < b; ++i)
#define fe(i, a, b) for(i = a; i <= b; ++i)
#define rf(i, a, b) for(i = a; i > b; --i)
#define rfe(i, a, b) for(i = a; i >= b; --i)
#define pb(x) push_back(x)
#define pf(x) push_front(x)
#define make_pair mp
#define DB(x) cout<<"\n"<<#x<<" = "<<(x)<<"\n";
#define CL(a, b) memset(a, b, sizeof(a));
#define F first
#define S second
#define boost ios_base::sync_with_stdio(false); cin.tie(NULL); cout.tie(NULL);
typedef pair<LL, LL> pll;
typedef vector<LL> vll;
typedef vector<pll> vpll;
const double PI = 3.14159265358979323846264338327950288419716939937510582097494459230;
ULL gcd(ULL a,ULL b){if(a==0)return b;if(b==0)return a;if(a==1||b==1)return 1;
if(a==b)return a;if(a>b)return gcd(b,a%b);else return gcd(a,b%a);}

int main(){
    //freopen("learning.sql", "r", stdin);
    freopen("count.txt", "w", stdout);
    //freopen("log.txt", "w", stderr);
    //boost;

    int i, j, k, n = 76;
    string query[n];
    int count[n];
    float number[] = {0.25, 0.21, 0.15, 0.07, 0.11, 0.06, 0.04, 0.08, 0.03};
    int roman[] = {300, 1000, 600, 700, 400, 4000, 1100, 600};

    count[0] = 150;     //w/o(bal and email)
    count[1] = 350;     //w/o(bal)
    count[2] = 200;     //w/o(email)
    count[3] = 600;     //w/o()
    k = 4;

    for(i = 0; i < sizeof(roman)/sizeof(int); i++)
        for(j = 0; j < sizeof(number)/sizeof(float); j++)
            count[k++] = roman[i] * number[j];

    for(i = 0; i < n; i++)
        cout<<count[i]<<endl;
    //    getline(cin, query[i]);
    //    for(j = 0; j < count[i]; j++)
    //        cout<<query[i]<<endl;

    return 0;
}
