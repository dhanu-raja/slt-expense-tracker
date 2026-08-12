import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import Login from './Login';
import { useAuthStore } from '../store/useAuthStore';
import api from '../lib/axios';

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

vi.mock('../lib/axios', () => ({
  default: {
    post: vi.fn(),
  },
}));

describe('Login Component', () => {
  const mockNavigate = vi.fn();
  const mockSetAuth = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNavigate as ReturnType<typeof vi.fn>).mockReturnValue(mockNavigate);
    (useAuthStore as unknown as ReturnType<typeof vi.fn>).mockImplementation((selector: any) =>
      selector({ setAuth: mockSetAuth })
    );
  });

  it('renders login form correctly', () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    expect(screen.getByText('Sign in to your account')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Email address')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Sign in' })).toBeInTheDocument();
  });

  it('shows error message on invalid credentials', async () => {
    (api.post as ReturnType<typeof vi.fn>).mockRejectedValueOnce(new Error('Invalid credentials'));

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Email address'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'wrongpass' } });
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument();
    });
  });

  it('calls setAuth and navigates on successful login', async () => {
    (api.post as ReturnType<typeof vi.fn>).mockResolvedValueOnce({
      data: { name: 'Test User', email: 'test@test.com', token: 'mockToken' },
    });

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByPlaceholderText('Email address'), { target: { value: 'test@test.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }));

    await waitFor(() => {
      expect(mockSetAuth).toHaveBeenCalledWith(
        { name: 'Test User', email: 'test@test.com' },
        'mockToken'
      );
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    });
  });
});
