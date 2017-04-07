export const promisify = (fn, Module) => (...args) => {
    return new Promise((resolve, reject) => {
        const handler = (error, resp) => {
            error ? reject(error) : resolve(resp);
        };
        args.push(handler);
        (typeof fn === 'function' ? fn : Module[fn])
            .call(Module, ...args);
    });
};

export default promisify
