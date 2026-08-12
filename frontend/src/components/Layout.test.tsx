import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import Layout from './Layout';
import { useAuthStore } from '../store/useAuthStore';

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

vi.mock('../store/useAuthStore', () => ({
  useAuthStore: vi.fn(),
}));

describe('Layout Component', () => {
  const mockNavigate = vi.fn();
  const mockLogout = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNavigate as ReturnType<typeof vi.fn>).mockReturnValue(mockNavigate);
    (useAuthStore as unknown as ReturnType<typeof vi.fn>).mockImplementation((selector: any) =>
      selector({ logout: mockLogout })
    );
  });

  it('renders sidebar with navigation links', () => {
    render(
      <MemoryRouter initialEntries={['/dashboard']}>
        <Layout />
      </MemoryRouter>
    );

    expect(screen.getByText('SLT Expense')).toBeInTheDocument();
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Expenses')).toBeInTheDocument();
    expect(screen.getByText('Income')).toBeInTheDocument();
  });

  it('calls logout and navigates to login when logout button is clicked', () => {
    render(
      <MemoryRouter initialEntries={['/dashboard']}>
        <Layout />
      </MemoryRouter>
    );

    const logoutBtn = screen.getByText('Logout');
    fireEvent.click(logoutBtn);

    expect(mockLogout).toHaveBeenCalledTimes(1);
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('displays the current path in the header', () => {
    render(
      <MemoryRouter initialEntries={['/expenses']}>
        <Layout />
      </MemoryRouter>
    );

    const header = screen.getByRole('heading', { level: 2 });
    expect(header).toHaveTextContent('expenses');
  });
});
