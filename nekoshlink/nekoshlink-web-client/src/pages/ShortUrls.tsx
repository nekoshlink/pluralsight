import React, { useState, useEffect }  from 'react';
import { useAuth } from "react-oidc-context";

const ShortUrls = () => {
    const [error, setError] = useState(null as any);
    const [isLoaded, setIsLoaded] = useState(false);
    const [data, setData] = useState(null as any);

    const auth = useAuth();

    useEffect(() => {
        if (auth.user?.access_token) {
            fetch("https://localhost:8443/api/v1/shorturls", {
                headers: {
                    "Authorization": `Bearer ${auth.user?.access_token}`
                }
            })
                .then(res => res.json())
                .then(
                    (data) => {
                        setIsLoaded(true);
                        setData(data);
                    },
                    (error) => {
                        setIsLoaded(true);
                        setError(error);
                    }
                )
        }
    }, [auth])

    if (error) {
        return (
            <div>
                <h1 className="m-5 text-green-800 text-3xl">Short URLs</h1>
                <p className="m-5 text-red-600 text-md">There was an error and the data could not be loaded</p>
            </div>
        );
    } else if (!isLoaded) {
        return (
            <div>
                <h1 className="m-5 text-green-800 text-3xl">Short URLs</h1>
                <p className="m-5 text-blue-800 text-md">Loading data...</p>
            </div>
        );
    } else {
        return (
            <div>
                <h1 className="m-5 text-green-800 text-3xl">Short URLs</h1>
                <ul className="m-5 pl-5 text-blue-800 text-md list-disc">
                    {data.results.map((item: any) => (
                        <li key={item.id}>
                            {item.shortCode} &rarr; <a className="text-blue-400 hover:text-blue-600" href={item.longUrl}>{item.longUrl}</a> <span className="italic text-sm">{item.tags?.join(", ") || ""}</span>
                        </li>
                    ))}
                </ul>
            </div>
        );
    }
}
export default ShortUrls;
