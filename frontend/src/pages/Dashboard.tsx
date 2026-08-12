import { useEffect, useState } from 'react';
import api from '../lib/axios';
import { TrendingUp, TrendingDown, DollarSign, PieChart } from 'lucide-react';

interface DashboardData {
  totalIncome: number;
  totalExpenses: number;
  currentBalance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  highestExpenseCategory: string;
  recentExpenses: any[];
  recentIncomes: any[];
}

const StatCard = ({ title, amount, icon: Icon, type }: any) => (
  <div className="bg-white rounded-xl shadow-sm border p-6 flex items-center gap-4">
    <div className={`p-4 rounded-full ${type === 'income' ? 'bg-green-100 text-green-600' : type === 'expense' ? 'bg-red-100 text-red-600' : 'bg-primary-100 text-primary-600'}`}>
      <Icon size={24} />
    </div>
    <div>
      <p className="text-sm text-gray-500 font-medium">{title}</p>
      <h3 className="text-2xl font-bold text-gray-900">${amount?.toFixed(2) || '0.00'}</h3>
    </div>
  </div>
);

const Dashboard = () => {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get('/dashboard');
        setData(response.data);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) return <div className="flex justify-center py-20">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard title="Total Balance" amount={data?.currentBalance} icon={DollarSign} type="balance" />
        <StatCard title="Total Income" amount={data?.totalIncome} icon={TrendingUp} type="income" />
        <StatCard title="Total Expenses" amount={data?.totalExpenses} icon={TrendingDown} type="expense" />
        <div className="bg-white rounded-xl shadow-sm border p-6 flex items-center gap-4">
          <div className="p-4 rounded-full bg-orange-100 text-orange-600">
            <PieChart size={24} />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Top Category (Month)</p>
            <h3 className="text-lg font-bold text-gray-900 capitalize">{data?.highestExpenseCategory || 'N/A'}</h3>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Expenses */}
        <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
          <div className="p-4 border-b bg-gray-50">
            <h3 className="font-semibold text-gray-800">Recent Expenses</h3>
          </div>
          <div className="divide-y">
            {data?.recentExpenses?.length === 0 ? (
              <p className="p-4 text-gray-500 text-center">No recent expenses.</p>
            ) : (
              data?.recentExpenses?.map((expense) => (
                <div key={expense.id} className="p-4 flex justify-between items-center hover:bg-gray-50">
                  <div>
                    <p className="font-medium text-gray-800">{expense.title}</p>
                    <p className="text-sm text-gray-500">{expense.category}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-red-600">-${expense.amount.toFixed(2)}</p>
                    <p className="text-sm text-gray-500">{new Date(expense.transactionDate).toLocaleDateString()}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Recent Incomes */}
        <div className="bg-white rounded-xl shadow-sm border overflow-hidden">
          <div className="p-4 border-b bg-gray-50">
            <h3 className="font-semibold text-gray-800">Recent Income</h3>
          </div>
          <div className="divide-y">
            {data?.recentIncomes?.length === 0 ? (
              <p className="p-4 text-gray-500 text-center">No recent income.</p>
            ) : (
              data?.recentIncomes?.map((income) => (
                <div key={income.id} className="p-4 flex justify-between items-center hover:bg-gray-50">
                  <div>
                    <p className="font-medium text-gray-800">{income.source}</p>
                    <p className="text-sm text-gray-500 truncate w-40">{income.note}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-green-600">+${income.amount.toFixed(2)}</p>
                    <p className="text-sm text-gray-500">{new Date(income.receivedDate).toLocaleDateString()}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
